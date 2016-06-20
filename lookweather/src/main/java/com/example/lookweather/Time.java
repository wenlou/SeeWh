package com.example.lookweather;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Time.
 */
public class Time {

	/**
	 * yyyy-MM-dd HH:mm:ss
	 *
	 * @return string
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowYMDHMSTime(){
		
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String date = mDateFormat.format(new Date());
		return date;
	}

	/**
	 * MM-dd HH:mm:ss
	 *
	 * @return string
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowMDHMSTime(){
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"MM-dd HH:mm:ss");
		String date = mDateFormat.format(new Date());
		return date;
	}

	/**
	 * MM-dd
	 *
	 * @return string
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowYMD(){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		String date = mDateFormat.format(new Date());
		return date;
	}

	/**
	 * yyyy-MM-dd
	 *
	 * @param date the date
	 * @return string
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getYMD(Date date){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		String dateS = mDateFormat.format(date);
		return dateS;
	}
}
