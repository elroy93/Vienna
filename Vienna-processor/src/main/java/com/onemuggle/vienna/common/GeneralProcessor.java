package com.onemuggle.vienna.common;

import com.sun.tools.javac.api.JavacTrees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Throwables.getStackTraceAsString;

@SupportedAnnotationTypes("*")
public class GeneralProcessor extends AbstractProcessor {

    // 异常记录，用于unitTest的assert
    private final List<String> exceptionStacks = Collections.synchronizedList(new ArrayList<>());

    public JavacTrees trees;

    /**
     * ============================================= methods ================================================
     */

    // 支持的注解类型
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        return super.getSupportedAnnotationTypes();
//    }

    // 支持的最新的类型
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }



    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            doProcess(annotations, roundEnv);
        } catch (Exception e) {
            String trace = getStackTraceAsString(e);
            exceptionStacks.add(trace);
            fatalError(trace);
        }
        return false;
    }

    private void doProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (roundEnv.processingOver()) {
             // System.out.println(">> processingOver");

        }else{
             // System.out.println(">> processingOver not ");
            Set<? extends Element> rootElements = roundEnv.getRootElements();
            for (Element rootElement : rootElements) {
                 // System.out.println(">> processingOver rootElement " + rootElement.toString());
            }
        }

        // 这里包含
        Set<? extends Element> elementsWithEcsSystems = roundEnv.getElementsAnnotatedWith(ReNamed.class);
         // System.out.println(">> EcsSystem " + elementsWithEcsSystems);

        for (Element elementsWithEcsSystem : elementsWithEcsSystems) {

            elementsWithEcsSystem.getSimpleName();

            Name simpleName = elementsWithEcsSystem.getSimpleName();
            ElementKind kind = elementsWithEcsSystem.getKind();
            Element enclosingElement = elementsWithEcsSystem.getEnclosingElement();
            List<? extends AnnotationMirror> annotationMirrors = elementsWithEcsSystem.getAnnotationMirrors();
            Set<Modifier> modifiers = elementsWithEcsSystem.getModifiers();
             // System.out.println(elementsWithEcsSystem);
        }


    }


    /**
     * ============================================= log ================================================
     */
    private void log(String msg) {
        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
        }
    }

    private void warning(String msg, Element element, AnnotationMirror annotation) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg, element, annotation);
    }

    private void error(String msg, Element element, AnnotationMirror annotation) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, element, annotation);
    }

    private void fatalError(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: " + msg);
    }

}
