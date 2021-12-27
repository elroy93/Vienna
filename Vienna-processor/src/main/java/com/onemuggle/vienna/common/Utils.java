package com.onemuggle.vienna.common;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class Utils {


    public static RoundEnvironment roundEnv;
    public static JavacProcessingEnvironment processingEnv;
    public static JavacTrees trees;
    public static TreeMaker treeMaker;
    public static Names names;
    public static Context context;


    public static Types types() {
        return processingEnv.getTypeUtils();
    }

    public static JCTree getClassDecl(Element element) {
        return JavacTrees.instance(Utils.processingEnv).getTree(element);
    }

    public static Filer filer() {
        return processingEnv.getFiler();
    }


    public static void log(String msg) {
        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
        }
    }

    public static void warning(String msg, Element element, AnnotationMirror annotation) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg, element, annotation);
    }

    public static void error(String msg, Element element, AnnotationMirror annotation) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, element, annotation);
    }

    public static void fatalError(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: " + msg);
    }

}
