package com.syswin.Tson.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
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
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor
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
        //Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(JsonType.class);
        TypeSpec.Builder builder = (TypeSpec.Builder) TypeSpec.classBuilder("TSON")
                .addModifiers(Modifier.PUBLIC);
//        for(Element element:elements){
//             processElement(element,builder);
//        }
        try {
            JavaFile.builder("com.syswin.tson.processor.TsonProcessor", builder.build()).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static final String SUFFIX = "$$ZYAO";

    private void analysisAnnotated(Element classElement)
    {
        JsonType annotation = classElement.getAnnotation(JsonType.class);
        String name = annotation.value();

//        TypeElement superClassName = mElementUtils.getTypeElement(name);
        String newClassName = name + SUFFIX;

        StringBuilder builder = new StringBuilder()
                .append("package com.zyao89.demoprocessor.auto;\n\n")
                .append("public class ")
                .append(newClassName)
                .append(" {\n\n") // open class
                .append("\tpublic String getMessage() {\n") // open method
                .append("\t\treturn \"");

        // this is appending to the return statement
        builder.append(name).append(" !\\n");


        builder.append("\";\n") // end return
                .append("\t}\n") // close method
                .append("}\n"); // close class


        try { // write the file
            JavaFileObject source = mFiler.createSourceFile("com.zyao89.demoprocessor.auto."+newClassName);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }

        info(">>> analysisAnnotated is finish... <<<");
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
