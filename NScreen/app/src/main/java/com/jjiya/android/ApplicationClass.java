/**
 * 
 */
package com.jjiya.android;

import android.app.Application;
import android.content.res.Configuration;

/**
 * @author swlim
 *
 */
public class ApplicationClass extends Application {

	private static ApplicationClass mAppInstance;
	private String mAppName;
	
	/** onCreate()
     * 액티비티, 리시버, 서비스가 생성되기전 어플리케이션이 시작 중일때 Application onCreate() 메서드가 만들어 진다고 나와 있습니다. by. Developer 사이트
     * 애플리케이션이 생성될 때 호출된다. 모든 상태변수와 리소스 초기화한다. 
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mAppInstance = this;
        mAppName = "C&M";
    }
 
    /**
     * onConfigurationChanged()
     * 컴포넌트가 실행되는 동안 단말의 화면이 바뀌면 시스템이 실행 한다.
     * 애플리케이션은 구성변경을 위해 재시작하지 않는다. 변경이 필요하다면 이곳에서 핸들러를 재정의 하면 된다.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
 
    /**
     * 애플리케이션 객체가 종료될 때 호출되는데 항상 보증하지 않는다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    
    public static ApplicationClass getInstance() {
        return mAppInstance;
    }
    
    
	public String getAppName() {
		return mAppName;
	}

//	public void setAppName(String appName) {
//		this.mAppName = appName;
//	}
    
    
}
