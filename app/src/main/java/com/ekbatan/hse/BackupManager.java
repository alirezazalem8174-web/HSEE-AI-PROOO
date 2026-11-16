package com.ekbatan.hse;

import android.content.*;import android.database.*;import android.database.sqlite.*;import java.io.*;import java.util.zip.*;

public final class BackupManager {
 public static File create(Context c,LibraryDb db)throws Exception{
  android.database.Cursor ck=db.getWritableDatabase().rawQuery("PRAGMA wal_checkpoint(TRUNCATE)",null);try{if(ck.moveToFirst()&&ck.getInt(0)!=0)throw new IOException("checkpoint دیتابیس کامل نشد");}finally{ck.close();}
  if(!db.integrityOk())throw new IOException("سلامت دیتابیس قبل از پشتیبان‌گیری تأیید نشد");
  File zip=new File(c.getCacheDir(),"HSE_AI_PRO_Backup_"+System.currentTimeMillis()+".zip");
  try(ZipOutputStream z=new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip)))){add(z,c.getDatabasePath("hse.db"),"hse.db");File dir=new File(c.getFilesDir(),"library");if(dir.exists())addDir(z,dir,"library/");}
  return zip;
 }
 private static void addDir(ZipOutputStream z,File d,String base)throws Exception{File[]fs=d.listFiles();if(fs==null)return;for(File f:fs){String n=base+f.getName();if(f.isDirectory())addDir(z,f,n+"/");else add(z,f,n);}}
 private static void add(ZipOutputStream z,File f,String name)throws Exception{if(!f.isFile())return;z.putNextEntry(new ZipEntry(name));try(InputStream in=new BufferedInputStream(new FileInputStream(f))){byte[]b=new byte[32768];int n;while((n=in.read(b))!=-1)z.write(b,0,n);}z.closeEntry();}
 public static void restore(Context c,File zip)throws Exception{
  File tmp=new File(c.getCacheDir(),"restore_"+System.nanoTime());if(!tmp.mkdirs())throw new IOException("ساخت فضای موقت ناموفق بود");
  File staged=new File(c.getCacheDir(),"hse_restore_staged.db"),oldDbBackup=new File(c.getCacheDir(),"hse_old_"+System.nanoTime()+".db"),oldLibBackup=new File(c.getCacheDir(),"hse_old_lib_"+System.nanoTime());boolean committed=false;
  try{
   extract(zip,tmp);File dbf=new File(tmp,"hse.db");if(!dbf.isFile())throw new IOException("database missing");copy(dbf,staged);validateDatabase(staged);
   File old=c.getDatabasePath("hse.db"),oldWal=new File(old.getPath()+"-wal"),oldShm=new File(old.getPath()+"-shm"),oldLib=new File(c.getFilesDir(),"library"),src=new File(tmp,"library");
   if(old.exists()&&!old.renameTo(oldDbBackup))throw new IOException("پشتیبان‌گیری موقت از دیتابیس فعلی ناموفق بود");if(oldWal.exists()&&!oldWal.delete())throw new IOException("حذف WAL قبلی ناموفق بود");if(oldShm.exists()&&!oldShm.delete())throw new IOException("حذف SHM قبلی ناموفق بود");
   if(oldLib.exists()&&!oldLib.renameTo(oldLibBackup))throw new IOException("پشتیبان‌گیری موقت از کتابخانه فعلی ناموفق بود");
   if(!staged.renameTo(old)){copy(staged,old);staged.delete();}
   if(src.exists())copyDir(src,oldLib);
   LibraryDb verify=new LibraryDb(c);boolean healthy=verify.integrityOk();verify.close();if(!healthy)throw new IOException("سلامت دیتابیس بازیابی‌شده تأیید نشد");
   delete(oldDbBackup);delete(oldLibBackup);committed=true;
  }finally{
   if(!committed){File old=c.getDatabasePath("hse.db"),oldWal=new File(old.getPath()+"-wal"),oldShm=new File(old.getPath()+"-shm"),oldLib=new File(c.getFilesDir(),"library");if(old.exists())old.delete();if(oldWal.exists())oldWal.delete();if(oldShm.exists())oldShm.delete();if(oldDbBackup.exists())oldDbBackup.renameTo(old);if(oldLib.exists())delete(oldLib);if(oldLibBackup.exists())oldLibBackup.renameTo(oldLib);}
   delete(tmp);if(staged.exists())staged.delete();if(oldDbBackup.exists())oldDbBackup.delete();if(oldLibBackup.exists())delete(oldLibBackup);
  }
 }
 private static void extract(File zip,File tmp)throws Exception{
  final long MAX_TOTAL=512L*1024*1024, MAX_ENTRY=128L*1024*1024; long total=0; java.util.HashSet<String> seen=new java.util.HashSet<>();
  try(ZipInputStream in=new ZipInputStream(new BufferedInputStream(new FileInputStream(zip)))){ZipEntry e;byte[]b=new byte[32768];
   while((e=in.getNextEntry())!=null){String n=e.getName();
    if(!seen.add(n))throw new IOException("پشتیبان دارای ورودی تکراری است");
    if(e.isDirectory()){if(!validDir(n))throw new IOException("پشتیبان نامعتبر است");continue;}
    if(!validFile(n))throw new IOException("فایل پشتیبان نامعتبر است");
    File f=new File(tmp,n),canonical=f.getCanonicalFile(),base=tmp.getCanonicalFile();
    if(!canonical.getPath().startsWith(base.getPath()+File.separator))throw new IOException("مسیر ناامن در پشتیبان");
    File parent=f.getParentFile();if(parent!=null&&!parent.exists()&&!parent.mkdirs())throw new IOException("ساخت مسیر بازیابی ناموفق بود");
    long entry=0;try(OutputStream out=new BufferedOutputStream(new FileOutputStream(f))){int k;while((k=in.read(b))!=-1){entry+=k;total+=k;if(entry>MAX_ENTRY||total>MAX_TOTAL)throw new IOException("حجم پشتیبان بیش از حد مجاز است");out.write(b,0,k);}}
   }
  }
 }
 private static void validateDatabase(File f)throws Exception{SQLiteDatabase d=SQLiteDatabase.openDatabase(f.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY);try{Cursor c=d.rawQuery("PRAGMA integrity_check",null);boolean ok=c.moveToFirst()&&"ok".equalsIgnoreCase(c.getString(0));c.close();if(!ok)throw new IOException("دیتابیس پشتیبان خراب است");Cursor t=d.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name IN ('documents','pages')",null);int n=0;while(t.moveToNext())n++;t.close();if(n<2)throw new IOException("ساختار اصلی دیتابیس پشتیبان ناقص است");}finally{d.close();}}
 private static boolean validDir(String n){return n.startsWith("library/")&&!n.contains("..")&&!n.startsWith("/");}
 private static boolean validFile(String n){return n.equals("hse.db")||(n.startsWith("library/")&&!n.contains("..")&&!n.startsWith("/"));}
 private static void copy(File a,File b)throws Exception{try(InputStream i=new BufferedInputStream(new FileInputStream(a));OutputStream o=new BufferedOutputStream(new FileOutputStream(b))){byte[]x=new byte[32768];int n;while((n=i.read(x))!=-1)o.write(x,0,n);}}
 private static void copyDir(File a,File b)throws Exception{b.mkdirs();File[]fs=a.listFiles();if(fs==null)return;for(File f:fs){File d=new File(b,f.getName());if(f.isDirectory())copyDir(f,d);else copy(f,d);}}
 private static void delete(File f){if(!f.exists())return;if(f.isDirectory()){File[]fs=f.listFiles();if(fs!=null)for(File x:fs)delete(x);}f.delete();}
}
