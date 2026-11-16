package com.ekbatan.hse;

import android.content.*;import android.net.Uri;import java.io.*;import java.security.*;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;import com.tom_roush.pdfbox.pdmodel.PDDocument;import com.tom_roush.pdfbox.text.PDFTextStripper;

public final class PdfImporter {
 public static class Result{public int pages,textPages;public String title;Result(int p,int t,String x){pages=p;textPages=t;title=x;}}
 public static Result importPdf(Context c,Uri uri,LibraryDb db,String title) throws Exception {
  PDFBoxResourceLoader.init(c);String safe=sanitize(title);File dir=new File(c.getFilesDir(),"library");if(!dir.exists()&&!dir.mkdirs())throw new IOException("ساخت پوشه کتابخانه ناموفق بود");
  File out=new File(dir,System.currentTimeMillis()+"_"+safe+".pdf");MessageDigest md=MessageDigest.getInstance("SHA-256");
  try(InputStream in=c.getContentResolver().openInputStream(uri);OutputStream os=new FileOutputStream(out)){if(in==null)throw new IOException("فایل PDF قابل خواندن نیست");byte[] b=new byte[32768];int n;while((n=in.read(b))!=-1){md.update(b,0,n);os.write(b,0,n);}}
  String hash=hex(md.digest());if(db.hasHash(hash)){out.delete();throw new IOException("این PDF قبلاً وارد شده است.");}
  long doc=-1;int pages=0,textPages=0;android.database.sqlite.SQLiteDatabase w=db.getWritableDatabase();w.beginTransaction();
  try(PDDocument pdf=PDDocument.load(out)){
   pages=pdf.getNumberOfPages();if(pages<=0||pages>10000)throw new IOException("تعداد صفحات PDF غیرمجاز است");
   doc=db.addDocument(safe,"عمومی",safe,out.getAbsolutePath(),hash);PDFTextStripper stripper=new PDFTextStripper();
   for(int p=1;p<=pages;p++){stripper.setStartPage(p);stripper.setEndPage(p);String tx=stripper.getText(pdf);if(tx!=null&&!tx.trim().isEmpty())textPages++;db.addPage(doc,p,tx==null?"":tx);}
   w.setTransactionSuccessful();
  }catch(Exception e){if(doc>0)db.deleteDocument(doc);out.delete();throw e;}finally{w.endTransaction();}
  return new Result(pages,textPages,safe);
 }
 private static String sanitize(String title){String s=title==null?"منبع PDF":title.trim();if(s.isEmpty())s="منبع PDF";s=s.replaceAll("[\\r\\n/\\\\:*?\"<>|]","_");if(s.length()>100)s=s.substring(0,100);return s;}
 private static String hex(byte[] b){StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format("%02x",x));return s.toString();}
}
