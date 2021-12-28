package com.onemuggle.vienna.common;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.tencent.vienna.processor.NamedRunnableProcessor;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


class GeneralProcessorTest {


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



