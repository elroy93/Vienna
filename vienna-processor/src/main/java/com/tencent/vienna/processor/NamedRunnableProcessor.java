package com.tencent.vienna.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;


@SupportedAnnotationTypes("*")
public class NamedRunnableProcessor extends AbstractProcessor {

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        try {
            super.init(processingEnv);
            Utils.processingEnv = (JavacProcessingEnvironment) processingEnv;
            Utils.trees = JavacTrees.instance(processingEnv);
            Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
            Utils.context = context;
            Utils.treeMaker = TreeMaker.instance(context);
            Utils.names = Names.instance(context);
        } catch (Throwable e) {
            Utils.log(">>>>> init NamedRunnableProcessor.class error, " + e);
        }
    }



    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            doProcessFirst(roundEnv);
        }
        return false;
    }

    private void doProcessFirst(RoundEnvironment roundEnv) {
        Set<? extends Element> rootElements = roundEnv.getRootElements();
        for (Element reNamedRootElement : rootElements) {
            JCTree tree = Utils.trees.getTree(reNamedRootElement);
            tree.accept(new ReNamedTreeTranslator(reNamedRootElement));
        }
    }


    /**
     * ###################### 配置 ######################
     */
    // 支持的最新的类型
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }


}
