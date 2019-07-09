package com.syswin.Tson.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.syswin.Tson.annotation.JsonType;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


@AutoService(Processor.class)
public class MsonProcessor extends AbstractProcessor
{
    private Types mTypeUtils;
    private Elements mElementUtils;
    private Filer    mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment)
    {
        super.init(processingEnvironment);

        //初始化我们需要的基础工具
        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        //支持的java版本
        return SourceVersion.RELEASE_7;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        //支持的注解
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(JsonType.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment)
    {//这里开始处理我们的注解解析了，以及生成Java文件
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(JsonType.class);
        TypeSpec.Builder builder = (TypeSpec.Builder) TypeSpec.classBuilder("TSON");
        for(Element element:elements){
             processElement(element,builder);
        }
        try {
            JavaFile.builder("com.syswin.tson.processor.TsonProcessor", builder.build()).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void processElement(Element element,TypeSpec.Builder builder) {

        MethodSpec.Builder toJsonMethodBuilder =
                MethodSpec.methodBuilder("toJson").addModifiers(Modifier.PUBLIC,
                        Modifier.STATIC).returns(String.class);
        toJsonMethodBuilder.addStatement("return $T.class.getCanonicalName()", ClassName.get
                ((TypeElement) element));
        builder.addMethod(toJsonMethodBuilder.build())
                .addModifiers(Modifier.PUBLIC);
    }

    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args));
    }
}
