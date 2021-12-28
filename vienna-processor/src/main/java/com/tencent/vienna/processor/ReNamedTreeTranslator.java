package com.tencent.vienna.processor;

import cn.hutool.core.lang.Assert;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.lang.model.element.Element;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class ReNamedTreeTranslator extends TreeTranslator {


    private final Element rootElements;

    public Stack<JCTree.JCClassDecl> jcClassDeclStack = new Stack<>();
    public JCTree.JCClassDecl currentClassDecl;

    public ReNamedTreeTranslator(Element rootElements) {
        this.rootElements = rootElements;
    }

    @Override
    public void visitClassDef(final JCTree.JCClassDecl classDeclaration) {
        currentClassDecl = classDeclaration;
        jcClassDeclStack.push(classDeclaration);
        super.visitClassDef(classDeclaration);
        Assert.state(jcClassDeclStack.pop() == classDeclaration, "classDeclaration is not the same as the one on the stack");
    }


    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        super.visitBlock(jcBlock);
        // TODO 查找field
        if (!jcBlock.toString().contains("@ReNamed(")) {
            return;
        }


        List<JCTree.JCStatement> blockStatements = jcBlock.getStatements();
        Map<Integer, InnerClazzMeta> idClazzJCVariableDeclMap = new HashMap<>();

        for (int i = 0; i < blockStatements.size(); i++) {
            JCTree.JCStatement jcStatement = blockStatements.get(i);
            if (jcStatement instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) jcStatement;
                List<JCTree.JCAnnotation> annotations = variableDecl.getModifiers().getAnnotations();
                java.util.List<JCTree.JCAnnotation> reNameAnnotations = annotations.stream().filter(jcAnnotation -> ((JCTree.JCIdent) jcAnnotation.annotationType).getName().toString().equals(ReNamed.class.getSimpleName())).collect(Collectors.toList());
                Assert.state(reNameAnnotations.size() <= 1, "reNameAnnotations.size() > 1");
                if (reNameAnnotations.size() == 1 && variableDecl.getInitializer() instanceof JCTree.JCNewClass && ((JCTree.JCNewClass) variableDecl.getInitializer()).def != null) {
                    String newClassName = null;
                    boolean isInterface = false;
                    List<JCTree.JCExpression> arguments = reNameAnnotations.get(0).getArguments();
                    for (JCTree.JCExpression argument : arguments) {
                        JCTree.JCAssign arg = (JCTree.JCAssign) argument;
                        JCTree.JCIdent lhs = (JCTree.JCIdent) arg.lhs;
                        JCTree.JCLiteral rhs = (JCTree.JCLiteral) arg.rhs;
                        if (lhs.name.toString().equals("value")) {
                            newClassName = rhs.getValue().toString();
                        }
                        if (lhs.name.toString().equals("isInterface")) {
                            isInterface = (boolean) rhs.getValue();
                        }
                    }

                    idClazzJCVariableDeclMap.put(i, new InnerClazzMeta(i, newClassName, variableDecl, isInterface));
                }
            }
        }

        if (idClazzJCVariableDeclMap.isEmpty()) {
            return;
        }

        idClazzJCVariableDeclMap.forEach((id, meta) -> {
            JCTree.JCClassDecl classDecl = meta.getClassDecl();
            classDecl.name = Utils.names.fromString(meta.getNewName());

            JCTree.JCVariableDecl oldVar = meta.getOldVar();
            JCTree.JCNewClass newClazz = ((JCTree.JCNewClass) oldVar.getInitializer());
            String fatherName = "";
            if (newClazz.clazz instanceof JCTree.JCTypeApply) {
                //处理有泛型类型的，比如new Callable<Integer>
                JCTree.JCTypeApply typeApply = (JCTree.JCTypeApply) newClazz.clazz;
                fatherName = typeApply.clazz.toString();
            }
            if (newClazz.clazz instanceof JCTree.JCIdent) {
                fatherName = newClazz.clazz.toString();
            }
            if (meta.isInterface) {
                classDecl.implementing = List.of(Utils.treeMaker.Ident(Utils.names.fromString(fatherName)));
            }else {
                classDecl.extending = Utils.treeMaker.Ident(Utils.names.fromString(fatherName));
            }

            meta.setFatherName(fatherName);
        });


        ListBuffer<JCTree.JCStatement> newBlockStatements = new ListBuffer<>();
        for (int i = 0; i < blockStatements.size(); i++) {
            InnerClazzMeta meta = idClazzJCVariableDeclMap.get(i);
            if (meta != null) {
                newBlockStatements.add(meta.getClassDecl());
                newBlockStatements.add(meta.getNewVar());
            } else {
                newBlockStatements.add(blockStatements.get(i));
            }
        }
        jcBlock.stats = newBlockStatements.toList();
    }


    static class InnerClazzMeta {

        private boolean isInterface;
        private Integer id;
        private String newName;
        private JCTree.JCVariableDecl oldVar;
        private String fatherName;

        public InnerClazzMeta(Integer id, String newName, JCTree.JCVariableDecl oldVar, boolean isInterface) {
            this.id = id;
            this.newName = newName;
            this.oldVar = oldVar;
            this.isInterface = isInterface;
        }

        public JCTree.JCVariableDecl getNewVar() {
            JCTree.JCNewClass jcNewClazz = (JCTree.JCNewClass) oldVar.getInitializer();
            jcNewClazz.def = null;
            jcNewClazz.clazz = Utils.treeMaker.Ident(Utils.names.fromString(newName));
            JCTree.JCVariableDecl tmp = oldVar;
            oldVar = null;
            return tmp;
        }


        public JCTree.JCClassDecl getClassDecl() {
            return ((JCTree.JCNewClass) oldVar.getInitializer()).def;
        }

        public Integer getId() {
            return id;
        }

        public String getNewName() {
            return newName;
        }

        public JCTree.JCVariableDecl getOldVar() {
            return oldVar;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
        }

        public boolean isInterface() {
            return isInterface;
        }

        public String getFatherName() {
            return fatherName;
        }
    }

}
