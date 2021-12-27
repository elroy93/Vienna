package com.onemuggle.vienna.common.old;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.onemuggle.vienna.common.Utils;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.lang.model.element.Element;
import java.util.Stack;

public class ReNamedTreeTranslator_Old extends TreeTranslator {


    private final Element rootElements;

    public Stack<JCTree.JCClassDecl> jcClassDeclStack = new Stack<>();
    public JCTree.JCClassDecl currentClassDecl;

    public ReNamedTreeTranslator_Old(Element rootElements) {
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
    public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
        super.visitMethodDef(jcMethodDecl);
        String s = jcMethodDecl.toString();


        if (!jcMethodDecl.getName().toString().equals("rStatic")) {
            return;
        }

        JCTree.JCBlock bodyBlock = jcMethodDecl.body;

        ListBuffer<JCTree.JCStatement> newLines = new ListBuffer<>();
        newLines.add(bodyBlock.stats.get(0));
        newLines.add(bodyBlock.stats.get(1));


        JCTree.JCNewClass newClassDecl = null;
        for (JCTree.JCStatement stat : bodyBlock.stats) {
            if (stat instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl variableDeclaration = (JCTree.JCVariableDecl) stat;
                JCTree.JCModifiers modifiers = variableDeclaration.getModifiers();
                if (CollectionUtil.isNotEmpty(modifiers.getAnnotations()) && modifiers.toString().contains("@ReNamed(")) {
                    String newClassName = (String) ((JCTree.JCLiteral) modifiers.getAnnotations().get(0).getArguments().get(0).getTree()).getValue();

                    JCTree.JCClassDecl def = ((JCTree.JCNewClass) variableDeclaration.getInitializer()).def;
                    newClassDecl = (JCTree.JCNewClass) variableDeclaration.getInitializer();
                    def.name = Utils.names.fromString(newClassName);
                    def.implementing = List.of(Utils.treeMaker.Ident(Utils.names.fromString("Runnable")));

                    newLines.add(def);
                }
            }

//            ((JCTree.JCClassDecl)((JCTree.JCNewClass)((JCTree.JCVariableDecl)bodyBlock.stats.get(1)).init).def).implementing = List.of(VennaUtils.treeMaker.Ident(VennaUtils.names.fromString("java.lang.Runnable")));
            System.out.println(" == newClassDecl \n" + newClassDecl);
        }

        newLines.add(bodyBlock.stats.get(3));
        newLines.add(bodyBlock.stats.get(4));
        newLines.add(bodyBlock.stats.get(5));

        bodyBlock.stats = newLines.toList();

    }

}
