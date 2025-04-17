package kr.co.pplus.store.pg.daou.util.File;

import kr.co.pplus.store.pg.daou.util.CommonUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileManager {
    String LocS_FileName = null;
    String LocS_DirName = null;
    PrintWriter Loc_printWrite = null;

    public FileManager() {
    }

    public void SetDirName(String InDirName) {
        this.LocS_DirName = InDirName;
    }

    public void SetFileName(String InFileName) {
        this.LocS_FileName = InFileName;
    }

    public void SetFileWriter() {
        try {
            this.Loc_printWrite = new PrintWriter(new FileWriter(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(this.LocS_DirName)))).append(File.separator).append(this.LocS_FileName))), true), true);
        } catch (Exception var2) {
        }

    }

    public void SetAutoFile(String InLogPath, String InFileName) {
        this.SetDirName(InLogPath);
        this.CreateDirs();
        this.SetFileName(String.valueOf(String.valueOf(InFileName)).concat(".log"));
        this.CreateFile();
        this.SetFileWriter();
    }

    public void FileDestory() {
        try {
            this.Loc_printWrite.close();
        } catch (Exception var2) {
        }

        this.Loc_printWrite = null;
    }

    public void FileInit() {
        this.LocS_FileName = null;
        this.LocS_DirName = null;
    }

    public void CreateDirs() {
        try {
            File file = new File(this.LocS_DirName);
            file.mkdirs();
            file = null;
        } catch (Exception var2) {
            System.out.println(var2.toString());
        }

    }

    public void CreateFile() {
        try {
            File file = new File(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(this.LocS_DirName)))).append(File.separator).append(this.LocS_FileName))));
            file.createNewFile();
            file = null;
        } catch (Exception var2) {
            System.out.println(var2.toString());
        }

    }

    public void println(String inLog) {
        try {
            String m_DateTime = CommonUtil.getDate("HH:mm:ss", CommonUtil.getDate());
            String m_Log = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(m_DateTime)))).append(" ").append(inLog)));
            this.Loc_printWrite.println(m_Log);
        } catch (Exception var8) {
            System.out.println("[ERROR] FileWriter :".concat(String.valueOf(String.valueOf(var8))));
        } finally {
            ;
        }

    }

    public void Blankln() {
        try {
            String m_Log = "";
            this.Loc_printWrite.println(m_Log);
        } catch (Exception var6) {
            System.out.println("[ERROR] FileWriter :".concat(String.valueOf(String.valueOf(var6))));
        } finally {
            ;
        }

    }

    public String getDirectory(String InFile) {
        File file;
        try {
            String LS_Directory = null;
            file = new File(InFile);
            if (!file.exists()) {
                System.out.println("File Not Found");
                Object var7 = null;
                return (String)var7;
            } else {
                LS_Directory = file.getCanonicalPath();
                int LI_LIndex = LS_Directory.lastIndexOf(File.separator);
                LS_Directory = LS_Directory.substring(0, LI_LIndex);
                return LS_Directory;
            }
        } catch (IOException var6) {
            System.out.println("[ERROR] ".concat(String.valueOf(String.valueOf(var6.getMessage()))));
            file = null;
            return "error";
        }
    }

    public boolean isDirectory(String InDir) {
        File file = new File(InDir);
        return file.isDirectory();
    }

    public boolean isFile(String InFile) {
        File file = new File(InFile);
        return file.exists();
    }

    public void LogWrite(String InFile, String InLog) {
        PrintWriter LP_PrintWriter = null;

        try {
            File file = new File(InFile);
            file.createNewFile();
            file = null;
            LP_PrintWriter = new PrintWriter(new FileWriter(InFile, true), true);
            LP_PrintWriter.println(InLog);
        } catch (Exception var14) {
            System.out.println("[ERROR] ErrorLogWriter :".concat(String.valueOf(String.valueOf(var14))));
        } finally {
            try {
                LP_PrintWriter.close();
            } catch (Exception var13) {
            }

            LP_PrintWriter = null;
        }

    }

    public static void main(String[] args) {
        FileManager fg = new FileManager();
        System.out.println(fg.getDirectory(args[0]));
        System.out.println(fg.isDirectory(args[1]));
    }
}
