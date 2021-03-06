/*
 * Copyright 2011 Christian Essl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package yeb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import java.security.MessageDigest;

import yeti.lang.Fun;

/**
 *
 * @author Christian
 */
public class YebUtils {

    public static String calcDigest(String algorithm, byte[] message)
		throws Exception
    {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(message);
		
        byte[] bytes = md.digest();
 
        StringBuilder hex = new StringBuilder();
    	for (int i=0;i<bytes.length;i++) {
    	  hex.append(Integer.toHexString(0xFF & bytes[i]));
    	}
 		return hex.toString();
    }


    static public Object moduleLoad(ClassLoader classLoader,String moduleName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        classLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            Class cl = classLoader.loadClass(moduleName);
            Method evalM = cl.getMethod("eval", new Class[]{});
            return evalM.invoke(null, new Object[]{});
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    static public Object moduleRun(ClassLoader classLoader,String moduleName, Object param) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        if(classLoader != null)
            Thread.currentThread().setContextClassLoader(classLoader);
        try {
            Fun fun = (Fun) moduleLoad(classLoader,moduleName);
            return fun.apply(param);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    public static String underlineToCamelCase(String name) {
        final int length = name.length();
        StringBuilder stb = new StringBuilder(length);
        boolean nextUpper = false;
        int i = 0;
        while (i < length) {
            char c = name.charAt(i);
            i = i + 1;
            if(c == '_')
                nextUpper = true;
            else {
                if (stb.length() == 0) {
                    stb.append(Character.toLowerCase(c));
                    nextUpper = false;
                }else {
                    if (nextUpper) {
                        stb.append(Character.toUpperCase(c));
                        nextUpper = false;
                    }else {
                        stb.append(c);
                    };
                }
            };        
        }
        return stb.toString();
    }
    

    public static String camelCaseToUnderline(String name) {
        final int length = name.length();
        boolean inupper = false;
        StringBuilder stb = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            char c = name.charAt(i);
            i = i + 1;
            if(inupper) {
                if(Character.isLowerCase(c)){
                    stb.append(c);
                    inupper = false;
                }else if(i < length && Character.isUpperCase(c) && Character.isLowerCase(name.charAt(i))){
                    stb.append('_');
                    stb.append(Character.toLowerCase(c));
                    inupper = false;
                }else{
                    stb.append(Character.toLowerCase(c));
                }
            }else{
                if(Character.isUpperCase(c)) {
                    stb.append('_');
                    stb.append(Character.toLowerCase(c));
                    inupper = true;
                }else{
                    stb.append(c);
                }
            }
        }
        return stb.toString();
    }

	
  public static byte[] generateRandomByteArray(int length) {
    byte[] line = new byte[length];
    for (int i = 0; i < length; i++) {
      //Choose a random byte - if we're generating delimited lines 
	  //then make sure we don't
      //choose first byte of delim
      byte rand = (byte) ((int) (Math.random() * 255) - 128);

      line[i] = rand;
    }
    return line;
  }

  public static String randomUnicodeString(int length) {
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      char c;
      do {
        c = (char) (0xFFFF * Math.random());
      } while ((c >= 0xFFFE && c <= 0xFFFF) || (c >= 0xD800 && c <= 0xDFFF)); 
	  //Illegal chars
      builder.append(c);
    }
    return builder.toString();
  }

  public static String randomAlphaString(int length) {
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      char c = (char) (65 + 25 * Math.random());
      builder.append(c);
    }
    return builder.toString();
  }




}
