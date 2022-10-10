package com.gavin.plugin.lifecycle;

import com.android.dex.ClassData;

import org.objectweb.asm.*;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class LifecycleMethodVisitor extends MethodVisitor {
    private String className;
    private String methodName;
    Label newStart = new Label();
    private int lineNumber = -1;
    //如果是false就不插，是true就插
    private boolean flag = false;

    public LifecycleMethodVisitor(MethodVisitor mv, String className, String methodName) {
        super(Opcodes.ASM6, mv);
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        visitLabel(newStart);
        File destFile = new File("D:/Android/Project/Android_Instrumentation/EventHandlerFilter/MethodList1.txt");
        //如果文件不存在就全插
        if (!destFile.exists() || destFile.length() == 0) {
            System.out.println("不存在!");
            flag = true;
            mv.visitLdcInsn(className);
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "realtimecoverage/MethodVisitor", "visit", "(Ljava/lang/String;Ljava/lang/String;)V", false);
        } else {
            //否则就插部分
            try {
                InputStreamReader Reader = new InputStreamReader(new FileInputStream(destFile), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(Reader);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.equals(className + "/" + methodName)) {
                        System.out.println("正在插桩：" + className + "/" + methodName);
                        flag = true;
                        mv.visitLdcInsn(className);
                        mv.visitLdcInsn(methodName);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "realtimecoverage/MethodVisitor", "visit", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                        break;
                    }
                }
                Reader.close();
            } catch (Exception e) {
                System.out.println("读取文件内容出错");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        if (newStart != null) {
            start = newStart;
            newStart = null;
            super.visitLineNumber(line, start);
            this.lineNumber = line;
            return;
        }
        // 这样做把第一次visitlinenumber强行转换到方法开始位置
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitInsn(int opcode) {
//        if (flag && ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW)) {
//            mv.visitLdcInsn(className);
////            mv.visitLdcInsn(methodName + "_" + lineNumber);
//            mv.visitLdcInsn(methodName);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "realtimecoverage/MethodVisitor", "visitFinish", "(Ljava/lang/String;Ljava/lang/String;)V", false);
//        }
//        super.visitInsn(opcode);

        if (flag && ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN))) {
            mv.visitLdcInsn(className);
//            mv.visitLdcInsn(methodName + "_" + lineNumber);
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "realtimecoverage/MethodVisitor", "visitFinish", "(Ljava/lang/String;Ljava/lang/String;)V", false);
        }
        else if (opcode == Opcodes.ATHROW) {
            mv.visitLdcInsn(className);
//            mv.visitLdcInsn(methodName + "_" + lineNumber);
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "realtimecoverage/MethodVisitor", "visitFinish", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            //如果是因为异常崩溃退出返回，线程等待2个throttle，待blockingqueue写完再崩溃
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "realtimecoverage/MethodVisitor", "tearDown", "()V", false);
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }


}