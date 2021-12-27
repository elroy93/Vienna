package com.onemuggle.vienna.common;

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

        // 在block中查找变量
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
                    String newClazzName = (String) ((JCTree.JCLiteral) reNameAnnotations.get(0).getArguments().get(0).getTree()).getValue();
                    idClazzJCVariableDeclMap.put(i, new InnerClazzMeta(i, newClazzName, variableDecl));
                }
            }
        }

        if (idClazzJCVariableDeclMap.isEmpty()) {
            return;
        }

        idClazzJCVariableDeclMap.forEach((id, meta) -> {
            JCTree.JCClassDecl classDecl = meta.getClassDecl();
            classDecl.name = Utils.names.fromString(meta.getNewName());
            classDecl.implementing = List.of(Utils.treeMaker.Ident(Utils.names.fromString("Runnable")));
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


//    @Override
//    public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
//        super.visitMethodDef(jcMethodDecl);
//        String s = jcMethodDecl.toString();
//
//
//        if (!jcMethodDecl.getName().toString().equals("rStatic")) {
//            return;
//        }
//
//        JCTree.JCBlock bodyBlock = jcMethodDecl.body;
//
//        ListBuffer<JCTree.JCStatement> newLines = new ListBuffer<>();
//        newLines.add(bodyBlock.stats.get(0));
//        newLines.add(bodyBlock.stats.get(1));
//
//
//        JCTree.JCNewClass newClassDecl = null;
//        for (JCTree.JCStatement stat : bodyBlock.stats) {
//            if (stat instanceof JCTree.JCVariableDecl) {
//                JCTree.JCVariableDecl variableDeclaration = (JCTree.JCVariableDecl) stat;
//                JCTree.JCModifiers modifiers = variableDeclaration.getModifiers();
//                if (CollectionUtil.isNotEmpty(modifiers.getAnnotations()) && modifiers.toString().contains("@ReNamed(")) {
//                    String newClassName = (String) ((JCTree.JCLiteral) modifiers.getAnnotations().get(0).getArguments().get(0).getTree()).getValue();
//
//                    JCTree.JCClassDecl def = ((JCTree.JCNewClass) variableDeclaration.getInitializer()).def;
//                    newClassDecl = (JCTree.JCNewClass) variableDeclaration.getInitializer();
//                    def.name = Utils.names.fromString(newClassName);
//                    def.implementing = List.of(Utils.treeMaker.Ident(Utils.names.fromString("Runnable")));
//
//                    newLines.add(def);
//                }
//            }
//
////            ((JCTree.JCClassDecl)((JCTree.JCNewClass)((JCTree.JCVariableDecl)bodyBlock.stats.get(1)).init).def).implementing = List.of(VennaUtils.treeMaker.Ident(VennaUtils.names.fromString("java.lang.Runnable")));
//            System.out.println(" == newClassDecl \n" + newClassDecl);
//        }
//
//        newLines.add(bodyBlock.stats.get(3));
//        newLines.add(bodyBlock.stats.get(4));
//        newLines.add(bodyBlock.stats.get(5));
//
//        bodyBlock.stats = newLines.toList();
//
//    }

    static class InnerClazzMeta {
        Integer id;
        String newName;
        JCTree.JCVariableDecl oldVar;
        JCTree.JCVariableDecl newVar;

        public InnerClazzMeta(Integer id, String newName, JCTree.JCVariableDecl oldVar) {
            this.id = id;
            this.newName = newName;
            this.oldVar = oldVar;
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
    }

}
