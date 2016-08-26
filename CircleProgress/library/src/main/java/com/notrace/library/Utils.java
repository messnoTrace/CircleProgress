package com.notrace.library;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public final class Utils {
	public static Bitmap small(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(0.3f, 0.3f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}



	/**
	 * 直接对对象的某个属性赋值
	 * 
	 * @param obj
	 * @param property
	 * @param value
	 * @throws Exception
	 */
	public static void setValue(Object obj, String property, Object value)
			throws Exception {
		Class<?> c = obj.getClass();
		Field f = c.getDeclaredField(property);
		f.set(obj, value);
	}

	/**
	 * 获取一个属性对应的Get函数返回值的类型
	 * 
	 * @param c
	 * @param property
	 * @return
	 */
	public static Class<?> getClassByPropertyName(Class<?> c, String property) {

		try {
			char ch = (char) property.charAt(0);
			String methodName = new StringBuffer(property).delete(0, 1)
					.insert(0, Character.toUpperCase(ch)).insert(0, "get")
					.toString();

			return c.getMethod(methodName, new Class<?>[0]).getReturnType();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过Get方法获取某个指定字段的值（String类型）
	 * 
	 * @param obj
	 * @param property
	 * @return
	 */
	public static String getValueStrByGetMethod(Object obj, String property) {
		try {
			Class<?> c = obj.getClass();

			char ch = (char) property.charAt(0);
			String methodName = new StringBuffer(property).delete(0, 1)
					.insert(0, Character.toUpperCase(ch)).insert(0, "get")
					.toString();

			Method method = c.getMethod(methodName);

			Object result = method.invoke(obj);
			if (result == null) {
				return null;
			}
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 通过Object的set方法设置属性值
	 * 
	 * @param obj
	 * @param property
	 * @param value
	 */
	public static void setValueBySetMehtod(Object obj, String property,
			Object value) {
		try {
			Class<?> c = obj.getClass();

			char ch = (char) property.charAt(0);
			String methodName = new StringBuffer(property).delete(0, 1)
					.insert(0, Character.toUpperCase(ch)).insert(0, "set")
					.toString();

			Method method = c.getMethod(methodName,
					getClassByPropertyName(c, property));

			method.invoke(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据Get方法获取请求参数列表
	 * 
	 * @param obj
	 * @return
	 */
	public static String getReqStrFromGetMethods(Object obj) {
		try {
			Class<?> c = obj.getClass();

			Method[] ms = c.getMethods();
			if (ms == null || ms.length == 0) {
				return null;
			}
			boolean isFirst = true;
			StringBuffer stringBuffer = new StringBuffer();

			for (int i = 0; i < ms.length; i++) {
				if (ms[i].getName().startsWith("get")
						&& !ms[i].getName().equals("getClass")) {
					String res = getValFromObj(ms[i], obj);
					if (res != null) {
						if (isFirst) {
							isFirst = false;
						} else {
							stringBuffer.append("&");
						}
						stringBuffer.append(getPropertyNameFromGetMethod(ms[i]
								.getName()));
						stringBuffer.append("=");
						stringBuffer.append(res);
					}
				}
			}
			return stringBuffer.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取特定的所有Get函数返回值的描述串
	 * 
	 * @param method
	 * @param obj
	 * @return
	 */
	public static String getValFromObj(Method method, Object obj) {
		try {
			Object result = method.invoke(obj);

			if (result == null) {
				return null;
			}
			if (result instanceof List<?>) {
				List<?> c = (List<?>) result;
				if (c.size() == 0) {
					return "|";
				}
				StringBuffer stringBuffer = new StringBuffer();
				// boolean isFirst = true;
				for (int i = 0; i < c.size(); i++) {
					Object o = c.get(i);
					if (o != null) {
						// if (isFirst) {
						// isFirst = false;
						// }
						// else {
						// stringBuffer.append("|");
						// }
						stringBuffer.append(o.toString());
						stringBuffer.append("|");
					}
				}
				return stringBuffer.toString();
			} else {
				return result.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取指定属性对应的标准Get方法
	 * 
	 * @param id
	 * @return
	 */
	public static String getPropertyNameFromGetMethod(String id) {
		String str = id.substring(3, id.length());
		char ch = (char) str.charAt(0);
		return new StringBuffer(str).delete(0, 1)
				.insert(0, Character.toLowerCase(ch)).toString();
	}

	// public static void writeByteArrayToFile(byte[] bytes, File file)
	// {
	// if (!file.exists()){
	// file.createNewFile();
	// }
	// FileOutputStream fileOutputStream = new FileOutputStream(file);
	// fileOutputStream.write(bytes);
	// }

	/**
	 * 从一个文件中读取内容，转化成byte[],注意必须是小文件
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static byte[] readByteArrayFromFile(File file) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int length = -1;
		while ((length = fileInputStream.read(buffer)) > 0) {
			byteArrayOutputStream.write(buffer, 0, length);
		}
		byteArrayOutputStream.flush();
		fileInputStream.close();

		byte[] result = byteArrayOutputStream.toByteArray();

		byteArrayOutputStream.close();

		return result;
	}

	/**
	 * 从一个输入流读出byte[],以输入流有EOF标志为准
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readByteArrayFromStream(InputStream inputStream)
			throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		while ((length = inputStream.read(buffer)) > 0) {
			byteArrayOutputStream.write(buffer, 0, length);
		}
		byteArrayOutputStream.flush();
		inputStream.close();

		byte[] result = byteArrayOutputStream.toByteArray();

		byteArrayOutputStream.close();

		return result;
	}

	/**
	 * 判断一个字符串是不是空串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.equals("");
	}

	// /**
	// * 隐藏手机号中间部分
	// *
	// * @param str
	// * @return
	// */
	// public static String ChangeMobileNum(String str) {
	// String mobileNum = null;
	// if (Global.MOBILE_NUM_LENGTH.equals(str.length())) {
	// String num = str.substring(0, 3);
	// String num1 = str.substring(7, 11);
	// mobileNum = num + "****" + num1;
	// }
	// return mobileNum;
	// }

	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	// public static Dialog createLoadingDialog(Context context, String msg) {
	//
	// LayoutInflater inflater = LayoutInflater.from(context);
	// View v = inflater.inflate(R.layout.wobaifu_loading_dialog, null);//
	// 得到加载view
	// LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);//
	// 加载布局
	// // main.xml中的ImageView
	// ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
	// // TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);//
	// // 提示文字
	// // 加载动画
	// Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
	// context, R.anim.loading_animation);
	// // 使用ImageView显示动画
	// spaceshipImage.startAnimation(hyperspaceJumpAnimation);
	// // tipTextView.setText(msg);// 设置加载信息
	//
	// Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);//
	// 创建自定义样式dialog
	//
	// loadingDialog.setCancelable(false);// 不可以用“返回键”取消
	// loadingDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
	// loadingDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕外部不取消
	//
	// loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
	// LinearLayout.LayoutParams.FILL_PARENT,
	// LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
	// return loadingDialog;
	//
	// }

	/**
	 * 检查当前网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Activity activity) {
		Context context = activity.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					System.out.println(i + "===状态==="
							+ networkInfo[i].getState());
					System.out.println(i + "===类型==="
							+ networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 手机号码号段
	 */
	public static final String[] MOBILE_HEADER = new String[] { "133", "153",
			"180", "181", "189", "130", "131", "132", "145", "155", "156",
			"185", "186", "134", "135", "136", "137", "138", "139", "147",
			"150", "151", "152", "157", "158", "159", "182", "183", "184",
			"187", "188", "170" };

	/**
	 * 校验手机号码是否正确
	 * 
	 * @param mbl
	 * @return
	 */
	public static boolean checkMblNo(String mbl) {
		if (Utils.isEmpty(mbl)) {
			return false;
		}

		Pattern pattern = Pattern.compile("^1[0-9]{10}$");
		Matcher matcher = pattern.matcher(mbl);

		if (!matcher.find()) {
			return false;
		}

		for (int i = 0; i < MOBILE_HEADER.length; i++) {
			String head = MOBILE_HEADER[i];
			if (mbl.startsWith(head)) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Toast
	 * 
	 * @param context
	 * @param contentOfToast
	 */
	public static void showToast(Context context, String contentOfToast) {
		Toast t3 = Toast.makeText(context, contentOfToast, Toast.LENGTH_LONG);
		t3.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
		t3.setMargin(0f, 0.5f);
		t3.show();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static String getVersion(Context context)// 获取版本号
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "null";
		}
	}

	/*** 清空空格以及回车等 ****/
	public static String getTrueString(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}

		if (dest.startsWith("\ufeff")) {
			return str.substring(1);
		} else {
			return dest;
		}

	}

	/** 根据屏幕分辨率返回正确的大小 **/
	public static float GETTRUESIZE(float textsize, Activity activity) {
		return dip2px(activity, textsize);
	}

	/*** 时间戳转换成时间 @return yyyy-MM-dd HH:mm:ss ***/
	public static String TimeStamp4Date(String timestampString) {
		try {
			Long timestamp = Long.parseLong(timestampString) * 1000;
			// :ss
			String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new java.util.Date(timestamp));
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知";
	}

	/*** 时间戳转换成时间 @return MM-dd ***/
	public static String TimeStamp4MomData(String timestampString) {
		try {
			Long timestamp = Long.parseLong(timestampString) * 1000;

			String date = new java.text.SimpleDateFormat("MM-dd")
					.format(new java.util.Date(timestamp));
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知";
	}
	 public static float dp2px(Resources resources, float dp) {
	        final float scale = resources.getDisplayMetrics().density;
	        return  dp * scale + 0.5f;
	    }

	    public static float sp2px(Resources resources, float sp){
	        final float scale = resources.getDisplayMetrics().scaledDensity;
	        return sp * scale;
	    }
}
