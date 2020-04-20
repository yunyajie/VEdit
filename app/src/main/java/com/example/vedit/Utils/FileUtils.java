package com.example.vedit.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.loader.content.CursorLoader;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Utils
 * @ClassName: FileUtils
 * @Description: java类作用描述
 * @Author: yunyajie
 * @CreateDate: 2020/4/20 15:30
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/20 15:30
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class FileUtils {
    private Context context;
    public FileUtils(Context context){
        this.context=context;
    }
    public String getFilePathByUri(Uri uri){
        //以file://开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())){
            return uri.getPath();
        }
        //以storage开头的也直接返回
        if (isOtherDocument(uri)){
            return uri.getPath();
        }
        //版本兼容的获取
        String path=getFilePathByUri_BELLOWAPI11(uri);
        if (path!=null){
            Log.d("FileUtils","getFilePathByUri_BELLOWAPI11获取到的路径为："+path);
            return path;
        }
        path=getFilePathByUri_API11to18(uri);
        if (path!=null){
            Log.d("FileUtils","getFilePathByUri_API11to18获取到的路径为："+path);
            return path;
        }
        path=getFilePathByUri_API19(uri);
        Log.d("FileUtils","getFilePathByUri_API19获取到的路径为："+path);
        return path;
    }

    private String getFilePathByUri_BELLOWAPI11(Uri uri) {
        //以content://开头的，比如content://media/external/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())){
            String path=null;
            String []projection=new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        return null;
    }
    private String getFilePathByUri_API11to18(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        String result = null;
        CursorLoader cursorLoader = new CursorLoader(context, contentUri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    private String getFilePathByUri_API19(Uri uri) {
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        if (split.length > 1) {
                            return Environment.getExternalStorageDirectory() + "/" + split[1];
                        } else {
                            return Environment.getExternalStorageDirectory() + "/";
                        }
                        // This is for checking SD Card
                    }
                } else if (isDownloadsDocument(uri)) {
                    //下载内容提供者时应当判断下载管理器是否被禁用
                    int stateCode = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
                    if (stateCode != 0 && stateCode != 1) {
                        return null;
                    }
                    String id = DocumentsContract.getDocumentId(uri);
                    // 如果出现这个RAW地址，我们则可以直接返回!
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    if (id.contains(":")) {
                        String[] tmp = id.split(":");
                        if (tmp.length > 1) {
                            id = tmp[1];
                        }
                    }
                    Uri contentUri = Uri.parse("content://downloads/public_downloads");
                    Log.d("测试打印Uri: " , uri.toString());
                    try {
                        contentUri = ContentUris.withAppendedId(contentUri, Long.parseLong(id));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String path = getDataColumn(context,contentUri, null, null);
                    if (path != null) return path;
                    // 兼容某些特殊情况下的文件管理器!
                    String fileName = getFileNameByUri(uri);
                    if (fileName != null) {
                        path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                        return path;
                    }
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context,contentUri, selection, selectionArgs);
                }
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getFileNameByUri(Uri uri) {
        String relativePath = getFileRelativePathByUri_API18(uri);
        if (relativePath == null) relativePath = "";
        final String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME
        };
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return relativePath + cursor.getString(index);
            }
        }
        return null;
    }

    private String getFileRelativePathByUri_API18(Uri uri) {
        final String[] projection;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            projection = new String[]{
                    MediaStore.MediaColumns.RELATIVE_PATH
            };
            try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH);
                    return cursor.getString(index);
                }
            }
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    private boolean isOtherDocument(Uri uri) {
        //以storage开头的也直接返回
        if (uri!=null&&uri.getPath()!=null){
            String path=uri.getPath();
            if (path.startsWith("/storage")){
                return true;
            }
            if (path.startsWith("/external_files")){
                return true;
            }
        }
        return false;
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
