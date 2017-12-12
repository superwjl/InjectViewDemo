# InjectViewDemo
```
@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.hello)
    TextView hello;

    @OnClick(R.id.hello)
    void clickHello(View view){
        hello.setText("Nice to meet you!");
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);
    }
}
```
```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentView {
    int value();
}
```
```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInject {
    int value();
}
```
```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnClick {
    int[] value();
}
```
```
public class ViewInjectUtils {
    public static void inject(Activity activity){
        injectContentView(activity);
        injectView(activity);
        injectEvent(activity);
    }

    private static void injectContentView(Activity activity){
        if(null == activity)return;
        Class<? extends Activity> clazz = activity.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if(contentView != null){
            //如果这个activity上面存在注解的话，就取出这个注解对应的value值，其实就是前面说的布局文件。
            int layoutId = contentView.value();
            try {
                Method setViewMethod = clazz.getMethod("setContentView", int.class);
                setViewMethod.invoke(activity, layoutId);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static void injectView(Activity activity){
        if(null == activity)return;
        Class<? extends Activity> clazz = activity.getClass();
        //获取activity的所有成员变量
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //获得每个成员变量上面的ViewInject注解，没有的话，就会返回null
            if(field.isAnnotationPresent(ViewInject.class)){
                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                int viewId = viewInject.value();
                field.setAccessible(true);
//                try {
//                    View view = activity.findViewById(viewId);
//                    field.set(activity, view);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
                try {
                    Method findViewByIdMethod = clazz.getMethod("findViewById", int.class);
                    Object view = findViewByIdMethod.invoke(activity, viewId);
                    field.set(activity, view);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void injectEvent(final Activity activity){
        if(null == activity)return;
        Class<? extends Activity> clazz = activity.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (final Method method : methods) {
            if(method.isAnnotationPresent(OnClick.class)){
                OnClick onClick = method.getAnnotation(OnClick.class);
                int[] value = onClick.value();
                method.setAccessible(true);
                Object listener = Proxy.newProxyInstance(View.OnClickListener.class.getClassLoader(),
                        new Class[]{View.OnClickListener.class}, new InvocationHandler() {
                            @Override
                            public Object invoke(Object o, Method method1, Object[] objects) throws Throwable {
                                return method.invoke(activity, objects);
                            }
                        });
                try {
                    for (int id : value) {
                        View v = activity.findViewById(id);
                        Method setOnClickListener = v.getClass().getMethod("setOnClickListener", View.OnClickListener.class);
                        setOnClickListener.invoke(v, listener);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
```
