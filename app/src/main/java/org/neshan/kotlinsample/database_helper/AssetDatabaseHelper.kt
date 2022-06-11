package org.neshan.kotlinsample.database_helper

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import kotlin.Throws
import android.database.sqlite.SQLiteException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Error
import kotlin.jvm.Synchronized

class AssetDatabaseHelper(private val myContext: Context) :
    SQLiteOpenHelper(myContext, DB_NAME, null, 1) {

    private val DB_PATH: String
    private var myDataBase: SQLiteDatabase? = null

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    @Throws(IOException::class)
    fun createDataBase() {

        //By calling this method and empty database will be created into the default system path
        //of your application so we are gonna be able to overwrite that database with our database.
        this.readableDatabase
        try {
            copyDataBase()
        } catch (e: IOException) {
            throw Error("Error copying database")
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private fun checkDataBase(): Boolean {
        var checkDB: SQLiteDatabase? = null
        try {
            val myPath = myContext.packageCodePath + DB_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: SQLiteException) {

            //database does't exist yet.
        }
        checkDB?.close()
        return if (checkDB != null) true else false
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    @Throws(IOException::class)
    private fun copyDataBase() {

        //Open your local db as the input stream
        val myInput = myContext.assets.open(DB_NAME)

        // Path to the just created empty db
        val outFileName = DB_PATH + DB_NAME

        //Open the empty db as the output stream
        val myOutput = FileOutputStream(outFileName, false)

        //transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int
        while (myInput.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }

        //Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    @Throws(SQLException::class)
    fun openDataBase(): SQLiteDatabase? {

        //Open the database
        val myPath = DB_PATH + DB_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)
        return myDataBase
    }

    @Synchronized
    override fun close() {
        if (myDataBase != null) myDataBase!!.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
    } // Add your public helper methods to access and get content from the database.

    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.
    companion object {
        private const val DB_NAME = "database.sqlite"
    }

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    init {
        DB_PATH = myContext.filesDir.path
    }
}