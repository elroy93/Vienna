package com.onemuggle.vienna.common;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


class GeneralProcessorTest {
//
//    public static void init() {
//        {
//
//            int m = 100;
//            class R_4 implements Runnable {
//                public void run() {
//                    System.out.println("r4" + m);
//                }
//            }
//
//            for (int i = 0; i < 2; i++) {
//                int x = 9;
//                @ReNamed("R_3")
//                Runnable r1 = new Runnable() {
//                    public void run() {
//                        System.out.println("r3" + m + x);
//                    }
//                };
//                int y = 7;
//                @ReNamed("R_11")
//                Runnable r11 = new Runnable() {
//                    public void run() {
//                        System.out.println("r11" + m + x);
//                    }
//                };
//                @ReNamed("R_22")
//                Runnable r22 = () -> {
//                    System.out.println("r22" + m + x);
//                };
//            }
//
//
//            new R_4().run();
//
//        }
//    }


    @Test
    public void test1() throws IOException {

        Compilation compilation = Compiler.javac()
                .withProcessors(new NamedRunnableProcessor())
                .compile(JavaFileObjects.forResource("A.java"));

        ImmutableList<JavaFileObject> javaFileObjects = compilation.generatedFiles();
        for (JavaFileObject javaFileObject : javaFileObjects) {
            InputStream inputStream = javaFileObject.openInputStream();
            File file = FileUtil.writeFromStream(inputStream, new File(javaFileObject.getName()));
            System.out.println(file.toURI());
        }


    }

}



