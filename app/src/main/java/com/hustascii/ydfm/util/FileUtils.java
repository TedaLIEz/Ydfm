package com.hustascii.ydfm.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.hustascii.ydfm.MyApp;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FileUtils {

    private static final String LOG_TAG = FileUtils.class.getSimpleName();

    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

    }

    public static final String getDictCacheFileFromLocal(Context context) {
        return getDictCacheFile(context, "dictCacheLocal");
    }

    public static final String getDictCacheFileFromServer(Context context) {
        return getDictCacheFile(context,"dictCacheServer");
    }
    public static final String getDictCacheFileFromSticker(Context context) {
        return getDictCacheFile(context,"sticker");
    }
    public static final String getThemeInstalledFile(Context context) {
        return getDictCacheFile(context,"themeInstalled");
    }

    public static final String getDictCacheFile(Context context,String folderName) {
        String userDir = "";
        if (FileUtils.isSDCardMounted()) {
            userDir = Environment.getExternalStorageDirectory().toString()
                    + System.getProperty("file.separator");
        } else {
            userDir = System.getProperty("file.separator");
        }
        String packageName = "";
        if(context != null){
            packageName = context.getPackageName();
        }
        File file = new File(userDir + packageName +"/"+folderName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/";
    }


    public static boolean isCacheExists(Context context, String fileName) {
        return fileIsExists(getCacheFolder(context) + "/" + fileName);
    }


    public static final String getImageCacheFile(Context context) {
        return StringUtils.getString(Environment.getExternalStorageDirectory(), "/", context.getPackageName(),
                "/cacheImage/");
    }

    public static void writeStringToFileCache(Context context, String fileName, String content) {
        if (context == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(content)) {
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());
            Log.e("cache", "file write to " + getCacheFolder(context) + "/" + fileName);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void removeFileCache(String fileName){
        try{
            if(TextUtils.isEmpty(fileName)){
                return;
            }
            Context ctx = MyApp.getContext();
            File f= ctx.getFileStreamPath(fileName);
            if(f!=null && f.exists()){
                f.delete();
            }
        }catch (Exception ex){

        }
    }
    public static String readStringFromFileCache(Context context, String fileName) {
        if (context == null || fileName == null) {
            return null;
        }
        try {
            // 打开文件输入流
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buff = new byte[1024];
            int hasRead = 0;
            StringBuilder sb = new StringBuilder("");
            // 读取文件内容
            while ((hasRead = fis.read(buff)) > 0) {
                sb.append(new String(buff, 0, hasRead));
            }
            // 关闭文件输入流
            fis.close();
            return sb.toString();
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * A方法追加文件：使用RandomAccessFile
     */
    public static void appendMethodA(String fileName, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * B方法追加文件：使用FileWriter
     */
    public static void appendMethodB(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * B方法追加文件：使用FileWriter
     */
    public static void writeMethodB(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, false);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();

        }
        return ret;
    }


    public static final HashMap loadFromStream(int id, HashMap hashMap, Resources resources) {

        try {
//            FileInputStream fis = new FileInputStream(path);
//            ObjectInputStream ois = new ObjectInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(resources.openRawResource(id));

            hashMap = (HashMap) ois.readObject();

        } catch (Exception e) {
            e.printStackTrace();
//            LogForTest.logW(" loadFromStream " + e);
        }


        return hashMap;

    }


    public static final void saveToStream(HashMap hashMap, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(hashMap);

            fos.close();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
//            LogForTest.logW(" saveToStream " + e);
        }
    }

    /**
     * 获取扩展名为@postfix的文件
     */
    public static final List<File> getFiles(String path, String postfix) {
        List<File> fileList = new ArrayList<>();
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            String[] fileNames = file.list();
            for (String fileName : fileNames) {
                if (fileName.endsWith(postfix)) {
                    File fileMeta = new File(path, fileName);
                    fileList.add(fileMeta);
                }
            }
        }
        return fileList;
    }

    public static final String getCacheFolder(Context context) {
        return context.getFilesDir().getPath();
    }


    /**
     * Get content via a InputStream
     *
     * @param inputStream
     * @return
     */
    public static String getStringFromInputStream(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    ///// zhiwei: object to file utlis
    private final static String sObjectsDir = "saved_objects";

    public static void removeObject(String file) {
        if (file == null)
            return;
        String path = MyApp.getContext()
                .getDir(sObjectsDir, Context.MODE_PRIVATE).getAbsolutePath()
                + "/" + file;
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
    }

    public static void saveObject(String fileName, Object o) {
        if (o == null || fileName == null) {
            return;
        }
        Object obj = getObject(fileName, o.getClass());
        if (obj != null && obj.equals(o)) {
            return;
        }
        MyApp app = MyApp.getContext();
        String dir = app.getDir(sObjectsDir, Context.MODE_PRIVATE)
                .getAbsolutePath();
        File file = new File(dir + File.separatorChar + fileName);
        if (file.exists()) {
            file.delete();
            file = new File(dir + File.separatorChar + fileName);
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(file));
            out.writeObject(o);
        } catch (Exception e) {
            //Log.e
        }
    }

    public static Object getObject(String fileName, Class<?> claz) {
        MyApp app = MyApp.getContext();
        String dir = app.getDir(sObjectsDir, Context.MODE_PRIVATE)
                .getAbsolutePath();
        File file = new File(dir + File.separatorChar + fileName);
        if (!file.isFile())
            return null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(
                    file));
            Object obj = in.readObject();
            in.close();
            if (obj != null && !claz.isInstance(obj)) {
                return null;
            }
            return obj;
        } catch (Exception e) {
            // Log.e()
        }
        return null;
    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }
    /////! zhiwei: object to file utlis


    public static void Copy(File oldfile, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldfile);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("error  ");
            e.printStackTrace();
        }
    }

    public static String replaceSlash(String path) {
        return path.replace("/","_");
    }
}
