package com.auribises.mycpdemo;

import android.net.Uri;

/**
 * Created by ishantkumar on 11/04/17.
 */

public class Util {

    // Information for my Database
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Students.db";

    // Information for my Table
    public static final String TAB_NAME = "Student";
    public static final String COL_ID = "_ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_PHONE = "PHONE";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_GENDER = "GENDER";
    public static final String COL_CITY = "CITY";

    public static final String CREATE_TAB_QUERY = "create table Student(" +
            "_ID integer primary key autoincrement," +
            "NAME varchar(256)," +
            "PHONE varchar(20)," +
            "EMAIL varchar(256)," +
            "GENDER varchar(10)," +
            "CITY varchar(256)" +
            ")";


    public static final String PREFS_NAME = "grocerytruck";
    public static final String KEY_NAME = "keyName";
    public static final String KEY_PHONE = "keyPhone";
    public static final String KEY_EMAIL = "keyEmail";

    // URI
    public static final Uri STUDENT_URI = Uri.parse("content://com.auribises.mycpdemo.studentprovider/"+TAB_NAME);


    final static String URI = "http://lovepreetgill.esy.es/lovu2017/";
    // URL
    //public static final String INSERT_STUDENT_JSP = "http://lovepreetgill.esy.es/lovu2017/insert.jsp";
    public static final String INSERT_STUDENT_PHP = "http://lovepreetgill.esy.es/lovu2017/insert.php";

    //public static final String RETRIEVE_STUDENT_JSP = "http://lovepreetgill.esy.es/lovu2017/retrieve.jsp";
    public static final String RETRIEVE_STUDENT_PHP = "http://lovepreetgill.esy.es/lovu2017/retrieve.php";

    //public static final String DELETE_STUDENT_JSP = "http://lovepreetgill.esy.es/lovu2017/delete.jsp";
    public static final String DELETE_STUDENT_PHP = "http://lovepreetgill.esy.es/lovu2017/delete.php";

    //public static final String UPDATE_STUDENT_JSP = URI+"update.jsp";
    public static final String UPDATE_STUDENT_PHP = "http://lovepreetgill.esy.es/lovu2017/update.php";
}
