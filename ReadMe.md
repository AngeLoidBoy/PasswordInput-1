# PasswordInput #
密码输入框    

![](http://i4.piimg.com/567571/1b0798bc3c3e3cca.gif)  

![](/passwordInput.gif)  

![](http://yotuku.cn/link?url=BkTFp8HWl&tk_plan=free&tk_storage=tietuku&tk_vuid=0b1372f4-d75a-448f-b3c6-484b81b02b70&tk_time=2016111311)  

![](/PasswordDialog.jpg)

v1.5
  
- 增加了PasswordDialog，采用Buider模式，可直接使用
- 精简了代码和自定义属性，只保留主要功能，以降低代码大小和使用成本
- 获得焦点时可改变颜色，区分获得焦点的控件和未获得焦点的控件
- 具有过渡动画，在输入、删除、焦点切换时进行较好的过渡，解决用户使用时的突兀感
- 使字符方块固定为正方形，取消了长方形的情况

## 添加依赖 ##
### Step 1. Add the JitPack repository to your build file ###
Add it in your root build.gradle at the end of repositories:  

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}  

### Step 2. Add the dependency ###

	dependencies {
        compile 'com.github.EthanCo:PasswordInput:1.5.0'
	}

## PasswordInput使用 ##

在你的项目中加入PasswordInput Library，之后在xml使用即可  
	
	<com.ethanco.lib.PasswordInput
    android:id="@+id/passwordInput_first"
    android:layout_width="match_parent"
    android:layout_height="56dp"
    app:boxCount="6"
    app:focusedColor="@color/colorAccent"
    app:notFocusedColor="@color/colorPrimary" />

### 获得输入的内容 ###

	PasswordInput passwordInput = (PasswordInput) findViewById(R.id.passwordInput);
	String pwdFirst = passwordInput.getText().toString();

### 设置内容改变监听 ###

    passwordInput.setTextLenChangeListen(new PasswordInput.TextLenChangeListen() {
        @Override
        public void onTextLenChange(CharSequence text, int len) {
           //do something
        }
    });

### 可设置的自定义属性 ###

	<!--获得焦点时的颜色-->
    <attr name="focusedColor" format="color" />
    <!--未获得焦点时的颜色-->
    <attr name="notFocusedColor" format="color" />
    <!--背景色-->
    <attr name="backgroundColor" format="color" />
    <!--字符方块的数量-->
    <attr name="boxCount" format="integer" />
    <!--获得焦点时颜色是否改变-->
    <attr name="focusColorChangeEnable" format="boolean" />
    <!--圆点半径-->
    <attr name="dotRaduis" format="dimension" />  


## PasswordDialog使用 ##

	PasswordDialog.Builder builder = new PasswordDialog.Builder(MainActivity.this)
    .setTitle(R.string.please_input_password)  //Dialog标题
    .setBoxCount(4) //设置密码位数
    .setBorderNotFocusedColor(R.color.colorSecondaryText) //边框颜色
    .setDotNotFocusedColor(R.color.colorSecondaryText)  //密码圆点颜色
    .setPositiveListener(new OnPositiveButtonListener() { 
        @Override //确定
        public void onPositiveClick(DialogInterface dialog, int which, String text) {
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        }
    })
	.setNegativeListener(new DialogInterface.OnClickListener() {
        @Override //取消
        public void onClick(DialogInterface dialogInterface, int i) {
            Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_SHORT).show();
        }
    })
    .addCheckPasswordFilter(new CountCheckFilter()); //添加过滤器
	builder.create().show();  

### 添加过滤器 ###
可进行过滤器的添加，对于不符合输入要求的，返回为false，将不会执行NegativeListener回调。  

	public class CountCheckFilter implements ICheckPasswordFilter {
	    @Override
	    public boolean onCheckPassword(Context context, CharSequence password) {
	        if (password.length() != 4) {
	            Toast.makeText(context, "密码需为4位", Toast.LENGTH_SHORT).show();
	            return false;
	        }
	        return true;
	    }
	}

> 默认已添加密码为空情况的过滤器

### 设置确定按钮文字 ###

	builder.setPositiveText("确定")  

### 设置取消按钮文字 ###

	builder..setNegativeText("取消")

### 设置每个密码方块的Radius ###

	builder.setBoxRadius(3) //单位为dp  

### 每个密码间隔的距离 ###

	buidler.setBoxMarge(3) //单位为dp  

### 设置密码框和密码圆点的颜色 ###

	buidler.setBorderNotFocusedColor(R.color.colorSecondaryText)
          .setDotNotFocusedColor(R.color.colorSecondaryText)

### 改变按钮颜色 ###
如需更改PositiveButton和NevegateButton的颜色，将	

	<color name="colorAccent">#FF4081</color>  

复制到app的color.xml中修改即可。  

### 自定义Dialog布局 ###

#### 新建custom_dialog_password.xml ####
将PasswordInput的tag设为passwordInput_dialog  

	<?xml version="1.0" encoding="utf-8"?>
	<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:layout_width="wrap_content"
	    android:layout_height="wrap_content">
	
	    <com.ethanco.lib.PasswordInput
	        android:id="@+id/pwdInput_dialog"
	        android:layout_width="@dimen/dialog_password_input_width"
	        android:layout_height="@dimen/dialog_password_input_height"
	        android:layout_gravity="center"
	        android:layout_margin="8dp"
	        android:cursorVisible="false"
	        android:focusable="true"
	        android:inputType="number"
	        android:maxLength="4"
	        android:tag="passwordInput_dialog"
	        app:focusColorChangeEnable="false" />
	
	</FrameLayout>
	
#### 在代码中使用 ####

	PasswordDialog.Builder builder = new PasswordDialog.Builder(MainActivity.this, R.layout.custom_dialog_password)  
