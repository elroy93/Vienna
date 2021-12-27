package com.onemuggle.vienna.common.old;

import com.google.auto.common.MoreElements;
import com.google.common.collect.Lists;
import com.onemuggle.vienna.common.ReNamed;
import com.onemuggle.vienna.common.ReNamedTreeTranslator;
import com.onemuggle.vienna.common.Utils;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Symbol;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.List;
import java.util.Set;


@SupportedAnnotationTypes("*")
public class NamedRunnableProcessor_Old extends AbstractProcessor {

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        try {
            //  // System.out.println(">>>>> init NamedRunnableProcessor.class");
            super.init(processingEnv);
            Utils.processingEnv = (JavacProcessingEnvironment) processingEnv;
            Utils.trees = JavacTrees.instance(processingEnv);
            Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
            Utils.context = context;
            Utils.treeMaker = TreeMaker.instance(context);
            Utils.names = Names.instance(context);
        } catch (Throwable e) {
            //  // System.out.println(">>>>> init NamedRunnableProcessor.class error, " + e);
            Utils.log(">>>>> init NamedRunnableProcessor.class error, " + e);
        }
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            Set<? extends Element> rootElements = roundEnv.getRootElements();
            System.out.println(rootElements);
        } else {
            Set<? extends Element> rootElements = roundEnv.getRootElements();
            doProcessFirst(roundEnv);
        }
        return false;
    }

    private void doProcessFirst(RoundEnvironment roundEnv) {

        Set<? extends Element> rootElements = roundEnv.getRootElements();
//        for (Element rootElement : rootElements) {
//            List<? extends Element> enclosedElements = rootElement.getEnclosedElements();
//            for (Element enclosedElement : enclosedElements) {
//                List<? extends Element> enclosedElements1 = enclosedElement.getEnclosedElements();
//                //  // System.out.println(enclosedElements1);
//            }
//
//        }

        // 有需要改名字的类
        List<Element> reNamedRootElements = Lists.newArrayList();
        for (Element rootElement : rootElements) {
            TreePath path = Utils.trees.getPath(rootElement);
            JCTree.JCCompilationUnit jcUnit = (path == null) ? null : (JCTree.JCCompilationUnit) path.getCompilationUnit();
            if (jcUnit != null && jcUnit.toString().contains("@ReNamed")) {
                reNamedRootElements.add(rootElement);
            }
        }

        for (Element reNamedRootElement : reNamedRootElements) {
            JCTree tree = Utils.trees.getTree(reNamedRootElement);
            tree.accept(new ReNamedTreeTranslator(reNamedRootElement));
        }

        if (true) {
            return;
        }

        Set<? extends Element> reNamedFields = roundEnv.getElementsAnnotatedWith(ReNamed.class);
        for (Element field : reNamedFields) {

            // 字段的源码
//            JCTree.JCVariableDecl fieldVariableDecl = (JCTree.JCVariableDecl) VennaUtils.getClassDecl(field);
//            TypeMirror fieldTypeMirror = field.asType();
            Element fieldClazzElement = Utils.types().asElement(field.asType());
            TypeMirror fieldClazzTypeMirror = fieldClazzElement.asType();
//            TypeElement typeElement = VennaUtils.processingEnv.getElementUtils().getTypeElement("java.io.Serializable");

            ReNamed annotation = field.getAnnotation(ReNamed.class);

            // 新类的名称
            String newClazzName = annotation.value();
            // 获取原始类的类型
            Symbol.ClassSymbol outerClazz = (Symbol.ClassSymbol) field.getEnclosingElement();


            //
            TypeSpec.Builder newClazzBuilder = TypeSpec.classBuilder(outerClazz.getSimpleName() + "$" + newClazzName)
                    .addModifiers(fieldClazzElement.getModifiers().toArray(new Modifier[0]));

            if (fieldClazzElement.getKind().isInterface()) {
                newClazzBuilder.addSuperinterface(fieldClazzTypeMirror);
            } else {
                newClazzBuilder.superclass(fieldClazzTypeMirror);
            }

            JavaFile file = JavaFile.builder(MoreElements.getPackage(outerClazz).getQualifiedName().toString(), newClazzBuilder.build()).build();

            try {
                file.writeTo(Utils.filer());
                file.writeTo(System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }

            printDebugInfo(field);
        }


    }

    private void printDebugInfo(Element field) {
//        ReNamed annotation = field.getAnnotation(ReNamed.class);
//        String value = annotation.value();
//        //  // System.out.println(">>>>> value = " + value);
//
//        Symbol owner = ((Symbol.VarSymbol) field).owner;
//        //  // System.out.println(">>>>> owner = " + owner);
//
//        JCTree.JCVariableDecl classDecl = (JCTree.JCVariableDecl) VennaUtils.getClassDecl(field);
//
//        //  // System.out.println("");
    }


    /**
     * ###################### 配置 ######################
     */
    // 支持的最新的类型
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

//    @Override
//    public ImmutableSet<String> getSupportedAnnotationTypes() {
//        return ImmutableSet.of(ReNamed.class.getName());
//    }


}
