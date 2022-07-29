# 动画相关

[TOC]

本文源码已上传github: [WhiteNight123/AnimationDemo (github.com)](https://github.com/WhiteNight123/AnimationDemo)

## 属性动画

### 为什么引入属性动画

  在Android中,动画分为两类:**视图动画**(`View Animation`)和**属性动画**(`Property Animation`).其中,`View Animation`包括`Tween Animation`(**补间动画**)和`Frame Animation`(**逐帧动画**);`Property Animation`包括`ValueAnimation`和`ObjectAnimator`.

  属性动画（`Property Animation`）是在 Android 3.0后才提供的一种全新动画模式

  首先来看一下补间动画的缺陷:

- 补间动画是**只能够作用在View上**的。我们可以对一个Button、TextView、或者其它任何继承自View的组件进行动画操作，但是如果我们想要对一个非View的对象进行动画操作，补间动画就不起作用了。

- 补间动画只能够实现**移动、缩放、旋转和淡入淡出**这四种动画操作，那如果我们想对View的背景色进行动态地改变,补间动画是不能实现的. 补间动画机制就是使用硬编码的方式来完成的，功能限定死就是这些，基本上**没有任何扩展性**。

- 补间动画只是**改变了View的显示效果而已，而不会真正去改变View的属性**。比如说，现在屏幕的左上角有一个按钮，然后我们通过补间动画将它移动到了屏幕的右下角，现在你可以去点击一下这个按钮，点击事件是不会触发的，因为实际上这个按钮还是停留在屏幕的左上角，只不过补间动画将这个按钮绘制到了屏幕的右下角而已。

  新引入的**属性动画**机制已经不再是针对于View来设计的了，也不限定于只能实现移动、缩放、  旋转和淡入淡出这几种动画操作，同时也不再只是一种视觉上的动画效果了。它实际上是一种不断地对**值**进行操作的机制，并将值赋值到指定对象的指定属性上，可以是任意对象的任意属性。

**属性动画工作原理[重点]**

![安卓属性动画工作原理](https://s2.loli.net/2022/07/29/mRjNFA2nMCEJYbz.png)

[安卓属性动画工作原理 -查看大图](https://www.processon.com/view/link/62cbbd0763768906c5087f89)

### ValueAnimator的简单使用

  ValueAnimator这个动画是针对**值**的. ValueAnimator不会对控件执行任何操作,我们可以给它设定从哪个值运动到哪个值,通过监听这些值的渐变过程来自己操作控件.

```kotlin
        // 步骤1：设置动画属性的初始值 & 结束值
        val animator = ValueAnimator.ofInt(0, 400)
        // ofInt（）作用有两个
        // 1. 创建动画实例
        // 2. 将传入的Int参数进行平滑过渡:此处传入0和400,表示将值从0平滑过渡到400
        // 如果传入了多个Int参数 ,如:a,b,c ,则是先从a过渡到b,再从b过渡到C，以此类推
        // ValueAnimator.ofInt()内置了整型估值器,关于自定义估值器将在下面讲解

        // 步骤2：设置动画的播放各种属性
        // 设置动画运行的时长
        animator.duration = 500
        // 设置动画延迟播放时间
        animator.startDelay = 200
        // 设置动画重复播放次数
        // 动画播放次数 = infinite时,动画无限重复
        animator.repeatCount = 0
        // 设置重复播放动画模式
        // ValueAnimator.RESTART(默认):正序重放
        // ValueAnimator.REVERSE:倒序回放
        animator.repeatMode = ValueAnimator.RESTART
        // 设置插值器
        animator.interpolator = AccelerateInterpolator()
        //设置估值器,对于ofInt()和ofFloat(系统都有默认的估值器,只有ofObject才需要我们自己设置)
        //animator.setEvaluator(IntEvaluator())
        
        // 步骤3：将改变的值手动赋值给对象的属性值：通过动画的更新监听器
        // 设置'值'的更新监听器,值每次改变、变化一次,该方法就会被调用一次
        animator.addUpdateListener { animation ->
            // 获得改变后的值
            val currentValue = animation.animatedValue as Int
            // 步骤4：将改变后的值赋给对象的属性值
            View.setproperty（currentValue）；
            // 步骤5：刷新视图，即重新绘制，从而实现动画效果
            View.requestLayout();
        }
        // 启动动画
        animator.start()
```

效果图

<img src="https://s2.loli.net/2022/07/11/OWHiDkyCujQlSPA.gif" alt="demo1" style="zoom: 25%;" />

#### `ofInt`()

  首先看一下步骤1的ofInt()的函数

```Java
public static ValueAnimator ofInt(@NonNull int... values) {
    	// 创建动画对象
        ValueAnimator anim = new ValueAnimator();
    	// 将传入的值赋值给动画对象
        anim.setIntValues(values);
        // 返回动画
        return anim;
}
```

`ValueAnimator.ofInt（）`与`ValueAnimator.oFloat（）`仅仅只是在估值器上的区别：

- `ValueAnimator.oFloat（）`采用默认的浮点型估值器 (`FloatEvaluator`)
- `ValueAnimator.ofInt（）`采用默认的整型估值器（`IntEvaluator`）

#### 插值器(Interpolator)

  估值器是设置从初始值到结束值的逻辑

- 插值器(Interpolator)决定**值**的变化速率(匀速,加速...)

- 估值器(TypeEvaluator)决定**值**的具体变化数值

一些常用的插值器<a name="常用的插值器"></a>

| 插值器                           | 效果                                                   |
| :------------------------------- | :----------------------------------------------------- |
| AccelerateDecelerateInterpolator | 在动画开始与结束的地方速率改变比较慢，在中间的时候加速 |
| AccelerateInterpolator           | 在动画开始的地方速率改变比较慢，然后开始加速           |
| AnticipateInterpolator           | 开始的时候向后然后向前甩                               |
| AnticipateOvershootInterpolator  | 开始的时候向后然后向前甩一定值后返回最后的值           |
| BounceInterpolator               | 动画结束的时候弹起                                     |
| CycleInterpolator                | 动画循环播放特定的次数，速率改变沿着正弦曲线           |
| DecelerateInterpolator           | 在动画开始的地方快然后慢                               |
| LinearInterpolator               | 以常量速率改变                                         |
| OvershootInterpolator            | 向前甩一定值后再回到原来位置                           |

  效果图:

| AccelerateDecelerateInterpolator                             | AccelerateInterpolator                                       | AnticipateInterpolator                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="https://s2.loli.net/2022/07/27/2zw9v3yabpIiCG6.gif" alt="AccelerateDecelerateInterpolator" style="zoom:25%;" /> | <img src="https://s2.loli.net/2022/07/27/Bzu8V7Dsey3oKrZ.gif" alt="AccelerateInterpolator" style="zoom:25%;" /> | <img src="https://s2.loli.net/2022/07/27/UHwNOTLXpsChAb8.gif" alt="AnticipateInterpolator" style="zoom:25%;" /> |


| AnticipateOvershootInterpolator                              | BounceInterpolator                                           | CycleInterpolator                                            |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="https://s2.loli.net/2022/07/27/yBCnQiZLUIRxmwA.gif" alt="AnticipateOvershootInterpolator" style="zoom:25%;" /> | <img src="https://s2.loli.net/2022/07/27/iCHNk6EpMmIARJu.gif" alt="BounceInterpolator" style="zoom:25%;" /> | <img src="https://s2.loli.net/2022/07/27/bDlG15fxipUZw8d.gif" alt="CycleInterpolator" style="zoom:25%;" /> |

| DecelerateInterpolator                                       | OvershootInterpolator                                        |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="https://s2.loli.net/2022/07/27/MEUAOG9qiWmwY4z.gif" alt="DecelerateInterpolator" style="zoom: 25%;" /> | <img src="https://s2.loli.net/2022/07/27/Tt9d5vwrnPjYlBu.gif" alt="OvershootInterpolator" style="zoom:25%;" /> |

 先来看一下系统自带的插值器是如何实现的,如LinearInterpolator

```Java
public class LinearInterpolator extends BaseInterpolator implements NativeInterpolator 
    // 实现了BaseInterpolator接口
    public LinearInterpolator() {
    }

    public LinearInterpolator(Context context, AttributeSet attrs) {
    }

    public float getInterpolation(float input) {
        //直接把input返回,以当前动画进度作为动画的数值进度,这也就表示当前动画的数值进度与动画进度的时间一致,所以LinearInterpolator的数值进度是匀速增加的
        return input;
    }
 
}



public abstract class BaseInterpolator implements Interpolator {
    public BaseInterpolator() {
        throw new RuntimeException("Stub!");
    }
}


public interface Interpolator extends TimeInterpolator {
}


```

​    `LinearInterpolator`最终实现了`TimeInterpolator`,再看一下`TImeInterpolator`

```java
public interface TimeInterpolator {
    //参数var1:Float类型,取值范围0~1,表示当前动画的进度
    //返回值:表示当前时间想要显示的进度,取值可以超过1,也可以小于0.超过1表示已超过目标,小于0表示小于开始位置
    float getInterpolation(float var1);
}
```

  :warning:注意: **input**参数与任何我们设定的值没有关系,只与时间有关,随着时间的推移,动画的进度也自然的增加,input参数就代表了当前动画的进度,而返回值则表示动画的的当前数值进度.

  接下来我们自定义一个插值器,从上面分析来看,我们只需实现`TimeInterpolator`接口就行

```kotlin
class MyInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        return 1 - input
    }
}
```

  效果图:就是反转一下动画

<img src="https://s2.loli.net/2022/07/27/FZYJeW2QntgCyED.gif" alt="MyInterpolator" style="zoom: 25%;" />

#### 估值器(TypeEvaluator)

​    Evaluator是一个转换器,把插值器得到的小数进度转换为对应的数值位置

​    先看一下`ofInt()`的估值器`IntEvaluator`怎么实现的

```java
// InttEvaluator实现了TypeEvaluator接口
public class IntEvaluator implements TypeEvaluator<Integer> {
    // 重写了evaluate方法
    // 参数ftaction:动画的完成度(从插值器中返回的值)
    // startValue,endValue:动画的初始值和结束值
    // 返回值就是当前数值进度所对应的具体数值
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        // 当前的值 = 开始的值 + 显示进度 * (结束值 - 开始值)
        return (int)(startInt + fraction * (endValue - startInt));
    }
}
```

​    

  对于`ValueAnimator.ofObject（）`，没有系统默认实现，因为对**对象**的动画操作比较复杂，我们需自定义估值器,来告知系统如何进行从*初始对象*过渡到*结束对象*的逻辑

```kotlin
// 首先实现TypeEvaluator接口
class MyEvaluator : TypeEvaluator<Int> {
    // 重写rvaluator方法
    override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
        // 将正常的计算方法反转
        // 当前的值 = 结束的值 - 显示进度 * (结束值 - 开始值)
        return (endValue - fraction * (endValue - startValue)).toInt()
    }
}
```

  这个与上面自定义插值器显示的效果一样

  效果图:

<img src="D:\OneDrive\桌面\动画可见\gif\MyEvaluator.gif" alt="MyEvaluator" style="zoom: 25%;" />



  系统中还有一个估值器,`ArgbEvaluator`是用来实现颜色过渡转换,源码如下

```java
public class ArgbEvaluator implements TypeEvaluator {
    private static final ArgbEvaluator sInstance = new ArgbEvaluator();
    
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        //这个就和编程基础Ⅱ里求ip地址一样
        int startInt = (Integer) startValue;
        float startA = ((startInt >> 24) & 0xff) / 255.0f;
        float startR = ((startInt >> 16) & 0xff) / 255.0f;
        float startG = ((startInt >>  8) & 0xff) / 255.0f;
        float startB = ( startInt        & 0xff) / 255.0f;

        int endInt = (Integer) endValue;
        float endA = ((endInt >> 24) & 0xff) / 255.0f;
        float endR = ((endInt >> 16) & 0xff) / 255.0f;
        float endG = ((endInt >>  8) & 0xff) / 255.0f;
        float endB = ( endInt        & 0xff) / 255.0f;

        // 自然界线性增长的亮度是条曲线,需要转换为LinearRGB(计算机视觉)
        // 计算时用LinearRGB,显示时用sRGB
        startR = (float) Math.pow(startR, 2.2);
        startG = (float) Math.pow(startG, 2.2);
        startB = (float) Math.pow(startB, 2.2);

        endR = (float) Math.pow(endR, 2.2);
        endG = (float) Math.pow(endG, 2.2);
        endB = (float) Math.pow(endB, 2.2);
        
        // compute the interpolated color in linear space
        // 转换公式与前面IntEvaluator一致
        float a = startA + fraction * (endA - startA);
        float r = startR + fraction * (endR - startR);
        float g = startG + fraction * (endG - startG);
        float b = startB + fraction * (endB - startB);

        // convert back to sRGB in the [0..255] range
        // 归一化
        a = a * 255.0f;
        r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
        g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
        b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }
} 
```

#### `ofObject`()

  `ifInt()`和`ofFloat()`都有系统内置的估值器(`FloatEvaluator`&`IntEvaluato`r),但`ofObject()`没有系统默认的实现,因此,需要我们自定义估值器(`TypeEvaluator`)来实现从初始对象过渡到结束对象的逻辑.

  先看看`ofObject()`的函数

```Java
// 第一个参数是自定义的估值器,第二个是可变长参数 
public static ValueAnimator ofObject(TypeEvaluator evaluator, Object... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setObjectValues(values);
        anim.setEvaluator(evaluator);
        return anim;
    }
```

下面看一个例子,一个抛球的动画

```kotlin
// 用Point记录球的位置
val animator = ValueAnimator.ofObject(FallingBallEvaluator(), Point(0, 0), Point(500, 500))
        animator.addUpdateListener { animation ->
            val mCurPoint = animation.animatedValue as Point
            ball.layout(
                mCurPoint.x,
                mCurPoint.y,
                mCurPoint.x + ball.width,
                mCurPoint.y + ball.height
            )
        }
        animator.duration = 2000
        animator.start()
    }

//自定义的估值器
class FallingBallEvaluator : TypeEvaluator<Point> {
    private val point = Point()
    override fun evaluate(fraction: Float, startValue: Point, endValue: Point): Point {
        point.x = (startValue.x + fraction * (endValue.x - startValue.x)).toInt()
        // y轴下落比x轴快
        if (fraction * 2 <= 1) {
            point.y = (startValue.y + fraction * 2 * (endValue.y - startValue.y)).toInt()
        } else {
            point.y = endValue.y
        }
        return point
    }
}
```

  效果图:

<img src="D:\OneDrive\桌面\动画可见\gif\ofObject.gif" alt="ofObject" style="zoom: 25%;" />

### ObjectAnimator的简单使用

  `ObjectAnimator`与 `ValueAnimator`类的区别：

- `ValueAnimator` 类是先改变值，然后**手动赋值**给对象的属性从而实现动画；是**间接**对**对象**属性进行操作
- `ObjectAnimator` 类是先改变值，然后**自动赋值**给对象的属性从而实现动画；是**直接**对 **对象**属性进行操作 

```kotlin
	//因为ObjectAnimator继承ValueAnimator,所以二者的用法很像
	//这里ofFloat的第一参数是指定要操作哪个控件,第二个参数是这个控件的那个属性,第三个参数是可变长参数,是指这个属性值如何变化
	val animator = ObjectAnimator.ofFloat(object, property, values)
		animator.duration = 500
         animator.start()
```

下面看几个例子

```kotlin
        button1.setOnClickListener {
            //从0旋转到360°
            val animator = ObjectAnimator.ofFloat(textView, "rotation", 0f, 360f)
            animator.duration = 1000
            animator.start()
        }
        button2.setOnClickListener {
            //在X轴上平移300f
            val curTranslationX = textView.translationX
            val animator = ObjectAnimator.ofFloat(
                textView,
                "translationX",
                curTranslationX,
                300f,
                curTranslationX
            )
            animator.duration = 1000
            animator.start()
        }
        button3.setOnClickListener {
            //在X轴上放大2倍再还原
            val animator = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 3f, 1f)
            animator.duration = 1000
            animator.start()

        }
        button4.setOnClickListener {
            //透明度从1到0再到1
            val animator = ObjectAnimator.ofFloat(textView, "alpha", 1f, 2f, 1f)
            animator.duration = 1000
            animator.start()
        }
```

效果图:

| 旋转                                                         | 平移                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="https://s2.loli.net/2022/07/27/gF1P5G4cJeTI87p.gif" alt="ObjectAnimator-旋转" style="zoom:33%;" /> | <img src="https://s2.loli.net/2022/07/27/dh1uGvkzgPVJi4p.gif" alt="ObjectAnimator-平移" style="zoom:33%;" /> |

| 透明                                                         | 缩放                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="https://s2.loli.net/2022/07/27/cr1awb7nK4MvixX.gif" alt="ObjectAnimator-透明" style="zoom: 33%;" /> | <img src="https://s2.loli.net/2022/07/27/RbSHJwYFsu3Lk46.gif" alt="ObjectAnimator-缩放" style="zoom:33%;" /> |

常用的属性值

| 属性         | 作用        |
| ------------ | ----------- |
| Rotation     | 绕Z轴旋转   |
| RotationY    | 绕Y轴旋转   |
| RotationX    | 绕X轴旋转   |
| TranslationX | 在X轴上平移 |
| TranslationY | 在Y轴上平移 |
| ScaleX       | 在X轴上缩放 |
| ScaleY       | 在Y轴上缩放 |
| Alpha        | 透明度      |

#### 自定义ObjectAnimator属性

  先理一下`ObjectAnimator`的动画设置流程:`OnjectAnimator`需要指定操作的控件对象,在动画开始后,先到控件类中去寻找设置属性所对应的`set()`函数,然后把动画中间值作为参数传给这个`set()`函数并执行它.所以我们自定义的控件中肯定存在一个`set()`函数与我们自定义属性相对应.

  还是前面那个抛球的例子

```java
//先从ImageView派生一个类表示球,并指定一个set函数
public class FallingBallImageView extends ImageView {
    public FallingBallImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //set函数对应的属性是fallingPos或FallingPos
    public void setFallingPos(Point pos) {
        layout(pos.x, pos.y, pos.x + getWidth(), pos.y + getHeight());
    }
}
```

```kotlin
//ObjectAnimation操作的控件是ball2,对应的属性是fallingPos,值从当前点运动到点(ball2.x-500, 500),其中自定义的FallingBallEvaluator和前面的一致
val animator = ObjectAnimator.ofObject(
                ball2,
                "fallingPos",
                FallingBallEvaluator(),
                Point(ball2.x.toInt(), 0),
                Point(ball2.x.toInt()-500, 500)
            )
            animator.duration = 1000
            animator.start()
```

  效果图:

<img src="https://s2.loli.net/2022/07/27/1OZoxCjlnV8YIUb.gif" alt="自定义ObjectAnimator属性" style="zoom:25%;" />



#### 关于`set()`&`get()`

  `ObjectAnimator`类自动赋给对象的属性的本质是调用该对象属性的`set()&get()`方法进行赋值,所以，`ObjectAnimator.ofFloat(Object object, String property, float ....values)`的第二个参数传入值的作用是：让`ObjectAnimator`类根据传入的属性名 去寻找 该对象对应属性名的 `set（）& get（）`方法，从而进行对象属性值的赋值.

  自动赋值的逻辑:

- 初始化时，如果属性的初始值没有提供，则调用属性的 `get（）`进行取值,如果没有`get()`会崩溃掉

-  当**值**变化时，用对象该属性的 `set（）`方法，从而从而将新的属性值设置给对象属性。

  对于属性动画，其拓展性在于：自定义对象的属性，并通过操作自定义的属性从而实现动画。

  自定义属性的步骤：

- 为对象设置需要操作属性的set（） & get（）方法
- 通过实现TypeEvaluator类从而定义属性变化的逻辑 

  设置对象类属性的`set（）` & `get（）`有两种方法：

1. 通过继承原始类，直接给类加上该属性的 `get（）`&  `set（）`,如上面的自定义`ObjectAnimation`
2. 通过包装原始动画对象，间接给对象加上该属性的 `get（）`&`set（）`,如下面的例子

```kotlin
// 创建WrapperView类,用于包装View对象
class WrapperView(private val mTarget: View) {
    var width: Int
    	// 设置set() & get()方法
        get() = mTarget.layoutParams.width
        set(width) {
            mTarget.layoutParams.width = width
            mTarget.requestLayout()
        }
}

// 得到包装类
val wrapper = WrapperView(textView)
// 直接操作包装类
val animator = ObjectAnimator.ofInt(wrapper, "width", 500)
animator.duration = 1000
animator.start()
```

  效果图:

<img src="https://s2.loli.net/2022/07/27/IEWRNeXbdMpHjYx.gif" alt="ObjectAnimator-改变set&get" style="zoom:25%;" />



#### 监听动画

- `Animation`类通过监听动画开始 / 结束 / 重复 / 取消时刻来进行一系列操作

```java
public static interface AnimatorListener {
        //开始时调用
        void onAnimationStart(Animator animation);
        //结束调用
        void onAnimationEnd(Animator animation);
        //取消时调用
        void onAnimationCancel(Animator animation);
        //重复时调用
        void onAnimationRepeat(Animator animation);
    }
```

**动画适配器AnimatorListenerAdapter**

  有时候我们并不需要监听动画的所有时刻,但`addListener(new AnimatorListener())`监听器是必须重写4个时刻方法，所以采用动画适配器（`AnimatorListenerAdapter`），只监听想要监听的时刻

```kotlin
animator.addListener(object : AnimatorListenerAdapter() {
            // 向addListener()方法中传入适配器对象AnimatorListenerAdapter()
            // 由于AnimatorListenerAdapter中已经实现好每个接口,所以这里不用全部实现
            override fun onAnimationStart(animation: Animator) {
                // 需要单独重写想要监听的方法就可以
            }
        })
```

#### 组合动画（AnimatorSet类）

AnimatorSet提供了两个函数`playSequencially()`和`playTogether()`

- playSequentially()表示所有动画依次播放
- playTogether()表示所有动画一起播放

```java
	//第一个函数是我们最常用的,传入多个Animator对象,这些动画会一起播放
	public void playTogether(Animator... items) {
        if (items != null) {
            Builder builder = play(items[0]);
            for (int i = 1; i < items.length; ++i) {
                builder.with(items[i]);
            }
        }
    }
    public void playTogether(Collection<Animator> items) {
        if (items != null && items.size() > 0) {
            Builder builder = null;
            for (Animator anim : items) {
                if (builder == null) {
                    builder = play(anim);
                } else {
                    builder.with(anim);
                }
            }
        }
    }

  
//第一个函数是我们最常用的,传入多个Animator对象,这些动画会依次播放
    public void playSequentially(Animator... items) {
        if (items != null) {
            if (items.length == 1) {
                play(items[0]);
            } else {
                for (int i = 0; i < items.length - 1; ++i) {
                    play(items[i]).before(items[i + 1]);
                }
            }
        }
    }
    public void playSequentially(List<Animator> items) {
        if (items != null && items.size() > 0) {
            if (items.size() == 1) {
                play(items.get(0));
            } else {
                for (int i = 0; i < items.size() - 1; ++i) {
                    play(items.get(i)).before(items.get(i + 1));
                }
            }
        }
    }
```

  下面看一下具体的使用

```java
 // playSequentially()依次播放
 // 设置需要组合的动画效果
 val tvAnimator1 = ObjectAnimator.ofInt(
            textView, "textColor",
            0xffff00ff.toInt(), 0xffffff00.toInt(), 0xffff00ff.toInt()
        )
        val tvAnimator2 = ObjectAnimator.ofFloat(textView, "translationY", 0f, 300f, 0f)
        val tvAnimator3 = ObjectAnimator.ofFloat(textView, "rotation", 0f, 180f, 0f)
        // 创建组合动画的对象
        val animatorSet = AnimatorSet()
        // 根据需求组合动画
        animatorSet.playSequentially(tvAnimator1, tvAnimator2, tvAnimator3)
        animatorSet.duration = 1000
        animatorSet.start()
        
//playTogether()一起播放
val tvAnimator1 = ObjectAnimator.ofInt(
            textView, "textColor",
            0xffff00ff.toInt(), 0xffffff00.toInt(), 0xffff00ff.toInt()
        )
        val tvAnimator2 = ObjectAnimator.ofFloat(textView, "translationY", 0f, 300f, 0f)
        val tvAnimator3 = ObjectAnimator.ofFloat(textView, "rotation", 0f, 180f, 0f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(tvAnimator1, tvAnimator2, tvAnimator3)
        animatorSet.duration = 1000
        animatorSet.start()
```

效果图:

| PlayTogether                                                 | PlaySequentially                                             |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="https://s2.loli.net/2022/07/27/iGaWb2kcLHNBS6t.gif" alt="PlayTogether" style="zoom: 33%;" /> | <img src="https://s2.loli.net/2022/07/27/jRHqCGsl7rmWnpN.gif" alt="PlaySwquentially" style="zoom: 33%;" /> |





  除了`playSequentally()`和`playTogether()`外,Animator还有Builder帮助我们控制动画的播放顺序

```java
        // 播放当前动画
        public Builder play(Animator anim) {
        if (anim != null) {
            return new Builder(anim);
        }
        return null;
    }
        // 将现有动画延迟x毫秒后执行
        public Builder with(Animator anim) {
            Node node = getNodeForAnimation(anim);
            mCurrentNode.addSibling(node);
            return this;
        }

        // 将现有动画插入到传入的动画之前执行
        public Builder before(Animator anim) {
            Node node = getNodeForAnimation(anim);
            mCurrentNode.addChild(node);
            return this;
        }

        //将现有动画插入到传入的动画之后执行
        public Builder after(Animator anim) {
            Node node = getNodeForAnimation(anim);
            mCurrentNode.addParent(node);
            return this;
        }

        // 将现有动画延迟x毫秒后执行
        public Builder after(long delay) {
            // setup a ValueAnimator just to run the clock
            ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(delay);
            after(anim);
            return this;
        }
```

AnimatorSet可以添加监听器,对应的监听器如下,和前面的监听器一样

```java
public static interface AnimatorListener {
        //开始时调用
        void onAnimationStart(Animator animation);
        //结束调用
        void onAnimationEnd(Animator animation);
        //取消时调用
        void onAnimationCancel(Animator animation);
        //重复时调用
        void onAnimationRepeat(Animator animation);
    }
```

注意:

- AnimatorSet的监听函数只是用来监听AnimatorSet的状态,与它里面的动画无关
- AnimatorSet中没有设置循环的函数,所以动画执行一次就结束了,不会执行到onAnimationRepeat()中
- 在AnimatorSet中设置了动画时长和插值器,会覆盖原来单个ObjectAnimator的设置,但是`setStartDelay()`函数例外,它不会覆盖单个动画的延时

#### ViewPropertyAnimator

  属性动画对比原来的视图动画有很多的优点，属性动画可以对所有的对象做动画操作，但Android开发中需要做动画最多的还是View，如果只是对一个view做好几个属性动画，属性动画的写法是比较繁琐的。所以安卓3.1补充了ViewPropertyAnimator这个类,ViewPropertyAnimator专用于操作View动画，语法更加简洁，使用更加方便。

```kotlin
//将textView透明度变为0,并向x轴移动50,y轴移动100,使用起来非常简单        
textView.animate().alpha(0f).x(50f).y(100f)
```

注意:

- `animate()`会返回一个ViewPropertyAnimator对象,可以通过调用这个对象的函数来设置需要实现动画的属性

- 自动开始:我们不需要显示的调用`start()`函数.

- 流畅(Fluent):ViewPropertyAnimator拥有一个流畅的接口(Fluent Interface),他允许将许多个函数调用自然地串在一起,并把一个多属性的动画写成一行代码

  这是一些ViewPropertyAnimator常用的函数
  

  | 函数                        | 含义                                  |
  | :-------------------------- | :------------------------------------ |
  | alpha(float value)          | 设置透明度                            |
  | alphaBy(float value)        | 设置透明度增量                        |
  | rotation(float value)       | 绕Z轴旋转                             |
  | rotationBy(float value)     | 绕Z轴旋转增量                         |
  | rotationX(float value)      | 绕X轴旋转                             |
  | rotationXBy(float value)    | 绕X轴旋转增量                         |
  | rotationY(float value)      | 绕Y轴旋转                             |
  | rotation                    | 绕Y轴旋转增量                         |
  | scaleX(float value)         | 设置X轴方向的缩放大小                 |
  | scaleXBy(float value)       | 设置X轴方向的缩放大小增量             |
  | scaleY(float value)         | 设置Y轴方向的缩放大小                 |
  | scaleYBy(float value)       | 设置Y轴方向的缩放大小增量             |
  | translationX(float value)   | 沿X轴方向平移                         |
  | translationY(float value)   | 沿Y轴方向平移                         |
  | translationYBy(float value) | 设置沿Y轴平移增量                     |
  | x(float value)              | 相对于父容器左上角坐标在X轴方向的位置 |
  | xBy(float value)            | 相对于父容器左上角坐标在X轴方向的增量 |
  | y(float value)              | 相对于父容器左上角坐标在Y轴方向的位置 |
  | yBy(float value)            | 相对于父容器左上角坐标在Y轴方向的增量 |
  | z(float value)              | 相对于父容器左上角坐标在Z轴方向的位置 |
  | zBy(float value)            | 相对于父容器左上角坐标在Z轴方向的增量 |

#### XML实现

  属性动画一般是在代码中实现,如果需要复用的话可以写在xml中

  在res/animator文件夹下，创建animator_translation.xml文件。XML文件有四个标签可用.

```xml
<?xml version="1.0" encoding="utf-8"?>
<set>    <!--相当于AnimatorSet-->
    <animator />   <!--相当于ValueAnimator-->
    <objectAnimator>     <!--相当于ObjectAnimator-->
        <propertyValuesHolder />  <!--相当于PropertyValuesHolder-->
    </objectAnimator>
</set>

```

`set`标签对应代码的`AnimatorSet`,只有一个属性可以设置：`android:ordering`，取值：同时播放`together`、顺序播放`sequentially`。

`animator`标签对应代码的`ValueAnimator`，可以设置如下属性：

- `android:propertyName`:属性名
- `android:duration`:动画时长
- `android:valueType`:属性类型,`intType`、`floatType`、`colorType`、`pathType`
- `android:valueFrom`:属性初始值
- `android:valueTo`:属性结束值
- `android:repeatCount`:重复次数
- `android:repeatMode`:重复模式
- `android:interpolator`:插值器。
- `android:startOffset`:延迟，对应`startOffset()`延迟多少毫秒执行

`objectAnimator`属性对应代码`ObjectAnimator`,由于继承自`ValueAnimator`，所以属性相对多了`android:propertyName`。

示例：

```xml
<objectAnimator
        android:propertyName="ScaleX"
        android:duration="1000"
        android:interpolator="@android:interpolator/linear"
        android:repeatCount="0"
        android:repeatMode="restart"
        android:startOffset="100"
        android:valueFrom="0"
        android:valueTo="2" />
```



#### PropertyValuesHolder与KeyFrame

  PropertyValuesHolder这个类的意义就是，保存了动画过程中所需要操作的属性和对应的值。我们通过`ofFloat(Object target, String propertyName, float… values)`构造的动画，`ofFloat()`的内部实现其实就是将传进来的参数封装成 PropertyValuesHolder实例来保存动画状态。在封装成 PropertyValuesHolder实例以后，后期的各种操作也是以 PropertyValuesHolder 为主的。

创建PropertyValuesHolder实例的函数：

```java
public static PropertyValuesHolder ofFloat(String propertyName, float... values)  
public static PropertyValuesHolder ofInt(String propertyName, int... values)   
public static PropertyValuesHolder ofObject(String propertyName, TypeEvaluator evaluator,Object... values)  
public static PropertyValuesHolder ofKeyframe(String propertyName, Keyframe... values)  
```

  可以看到在`ObjectAnimator.ofFloat`中只比`PropertyValuesHolder.ofFloat()`多了一个target.

将构造的PropertyValuesHolder实例设置进 ObjectAnimator：

```java
public static ObjectAnimator ofPropertyValuesHolder(Object target,PropertyValuesHolder... values)
```

PropertyValuesHolder 的`ofObject()`的函数

```java
public static PropertyValuesHolder ofObject(String propertyName, TypeEvaluator evaluator,Object... values)  
```

**Keyframe**

  KeyFrame主要用于自定义控制动画速率，KeyFrame直译过来就是关键帧。一个关键帧必须包含两个原素，第一时间点，第二位置。所以这个关键帧是表示的是某个物体在哪个时间点应该在哪个位置上。

  KeyFrame使用流程如下：

```kotlin
		// 生成 Keyframe 对象；
 		val frame0 = Keyframe.ofFloat(0f, 0f)
         val frame1 = Keyframe.ofFloat(0.1f, -45f)
         val frame2 = Keyframe.ofFloat(1f, 0f)
		// 利用 PropertyValuesHolder.ofKeyframe()生成 PropertyValuesHolder 对象
         val frameHolder = PropertyValuesHolder.ofKeyframe("rotation", frame0, frame1, frame2)
		// ObjectAnimator.ofPropertyValuesHolder()生成对应的 Animator
         val animator: Animator = ObjectAnimator.ofPropertyValuesHolder(textView, frameHolder)
         animator.duration = 1000
         animator.start()
```

效果图:



注意：在设置`ObjectAnimator.ofPropertyValuesHolder()`时

- 如果去掉第 0 帧`Keyframe.ofFloat(0f, 0)`，将以传入的第一个关键帧为起始位置
- 如果去掉结束帧`Keyframe.ofFloat(1f,0)`，将以传入最后一个关键帧为结束位置
- 使用 Keyframe 来构建动画，至少要有两个或两个以上帧。

KeyFrame的一些常用方法(基本和前面一样)：

```java
// fraction：表示当前的显示进度, value：表示当前应该在的位置
public static Keyframe ofFloat(float fraction, float value)
	// 表示动画进度为0时，动画所在的数值位置为0
	Keyframe.ofFloat(0, 0)
	// 表示动画进度为 25%时，动画所在的数值位置为-20
	Keyframe.ofFloat(0.25f, -20f)；
	// 表示动画结束时，动画所在的数值位置为0
	Keyframe.ofFloat(1f,0)；
// 设置fraction参数，即Keyframe所对应的进度
public void setFraction(float fraction)	
// 设置当前Keyframe所对应的值
public void setValue(Object value)	
// 设置Keyframe动作期间所对应的插值器
public void setInterpolator(TimeInterpolator interpolator)
```



### 源码解读

#### ValueAnimator源码解读

  讲完了使用,我们看看动画是怎么跑起来的,我们先分析`ValueAnimator`,`ObjectAnimator`是继承`ValueAnimator`,我们分析完`ValueAnimator`后再看看`ObjectAniamtor`多做了哪些工作



  先从`animator.start()`开始

```java
// ValueAnimator
@Override
    public void start() {
        // 调用内部的start,参数playBackwards是否反向播放,一般为false
        start(false);
    }
```

  进入内部的`start(boolean)`方法

```java
// ValueAnimator
private void start(boolean playBackwards) {
        if (Looper.myLooper() == null) {
            throw new AndroidRuntimeException("Animators may only be run on Looper threads");
        }
        mReversing = playBackwards;
        mSelfPulse = !mSuppressSelfPulseRequested;
        if (playBackwards && mSeekFraction != -1 && mSeekFraction != 0) {
            if (mRepeatCount == INFINITE) {
                // Calculate the fraction of the current iteration.
                float fraction = (float) (mSeekFraction - Math.floor(mSeekFraction));
                mSeekFraction = 1 - fraction;
            } else {
                mSeekFraction = 1 + mRepeatCount - mSeekFraction;
            }
        }
    // 开始标志
        mStarted = true;
        mPaused = false;
        mRunning = false;
        mAnimationEndRequested = false;
        mLastFrameTime = -1;
        mFirstFrameTime = -1;
        mStartTime = -1;
        // 注册animation回调
        addAnimationCallback(0);

        if (mStartDelay == 0 || mSeekFraction >= 0 || mReversing) {
            // 开始动画
            startAnimation();
            if (mSeekFraction == -1) {
                setCurrentPlayTime(0);
            } else {
                setCurrentFraction(mSeekFraction);
            }
        }
    }
```

  都是一些变量初始化,有两个方法`addAnimationCallback(0);`,`startAnimation()`

```java
// ValueAnimator
private void startAnimation() {
        if (Trace.isTagEnabled(Trace.TRACE_TAG_VIEW)) {
            Trace.asyncTraceBegin(Trace.TRACE_TAG_VIEW, getNameForTrace(),
                    System.identityHashCode(this));
        }

        mAnimationEndRequested = false;
        // 进行一些初始化
        initAnimation();
        mRunning = true;
        if (mSeekFraction >= 0) {
            mOverallFraction = mSeekFraction;
        } else {
            mOverallFraction = 0f;
        }
        if (mListeners != null) {
            // 通知动画开始
            notifyStartListeners();
        }
    }
```

  里面调用了两个方法`initAnimation()`和`notifyStartListeners()`方法

```Java
 // ValueAnimation
 // 一些初始化操作
    void initAnimation() {
        if (!mInitialized) {
            int numValues = mValues.length;
            for (int i = 0; i < numValues; ++i) {
                mValues[i].init();
            }
            mInitialized = true;
        }
    }

private void notifyStartListeners() {
        if (mListeners != null && !mStartListenersCalled) {
            ArrayList<AnimatorListener> tmpListeners =
                    (ArrayList<AnimatorListener>) mListeners.clone();
            int numListeners = tmpListeners.size();
            for (int i = 0; i < numListeners; ++i) {
                // 回调listener 接口
                tmpListeners.get(i).onAnimationStart(this, mReversing);
            }
        }
        mStartListenersCalled = true;
    }
```

  都是一些初始化的操作,没有开始动画,我们回头看看`addAnimationCallback()`方法

```java
// ValueAnimator
private void addAnimationCallback(long delay) {
        if (!mSelfPulse) {
            return;
        }
        // AnimationHandler 一个处理动画的类
        getAnimationHandler().addAnimationFrameCallback(this, delay);
    }
```

  看到这里调用了`AnimationHandler`,一个处理动画的单例类,并且把`this`传了进去

```java
// AnimationHandler
public void addAnimationFrameCallback(final AnimationFrameCallback callback, long delay) {
        // mAnimationFrameCallback 是ArrayList列表,储存了AnimationFrameCallback 对象
        // 当mAnimationFrameCallback=0时,第一次执行动画
        if (mAnimationCallbacks.size() == 0) {
            getProvider().postFrameCallback(mFrameCallback);
        }
        if (!mAnimationCallbacks.contains(callback)) {
            mAnimationCallbacks.add(callback);
        }
    // delay是setStartDelay()设置的延迟执行
        if (delay > 0) {
            mDelayedCallbackStartTime.put(callback, (SystemClock.uptimeMillis() + delay));
        }
    }
```

当`mAnimationFrameCallback=0`时

```java
// AnimationHandler
private AnimationFrameCallbackProvider getProvider() {
        if (mProvider == null) {
            // 直接new一个
            mProvider = new MyFrameCallbackProvider();
        }
        return mProvider;
    }

// AnimationHandler MyFrameCallbackProvider
@Override
        public void postFrameCallback(Choreographer.FrameCallback callback) {
            // Choreographer 负责刷新注册监听以及提供回调接口供底层调用
            mChoreographer.postFrameCallback(callback);
        }


// Choreographer
public void postFrameCallback(FrameCallback callback) {
        postFrameCallbackDelayed(callback, 0);
    }

public void postFrameCallbackDelayed(FrameCallback callback, long delayMillis) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
    // 内部调用了 postCallbackDelayedInternal()
        postCallbackDelayedInternal(CALLBACK_ANIMATION,
                callback, FRAME_CALLBACK_TOKEN, delayMillis);
    }
```

  `Choreographer`内部有几个队列，上面方法的第一个参数 `CALLBACK_ANIMATION`就是用于区分这些队列的，而每个队列里可以存放 `FrameCallback`对象，也可以存放 Runnable 对象。Animation 动画原理上就是通过 ViewRootImpl 生成一个 `doTraversal()` 的 Runnable 对象（其实也就是遍历 View 树的工作）存放到 Choreographer 的队列里的。而这些队列里的工作，都是用于在接收到屏幕刷新信号时取出来执行的。但有一个关键点，Choreographer 要能够接收到屏幕刷新信号的事件，是需要先调用 Choreographer 的 `scheduleVsyncLocked()` 方法来向底层注册监听下一个屏幕刷新信号事件的。  

  **小结:**当`ValueAnimator`调用了`start()`方法之后，首先会对一些变量进行初始化工作并通知动画开始了，然后`ValueAnimator`实现了 `AnimationFrameCallback`接口，并通过`AnimationHander`将自身 this 作为参数传到`mAnimationCallbacks`列表里缓存起来。而`AnimationHandler`在`mAnimationCallbacks`列表大小为 0 时会通过内部类`MyFrameCallbackProvider`将一个`mFrameCallback`工作缓存到`Choreographer`的待执行队列里，并向底层注册监听下一个屏幕刷新信号事件。

  当接收到屏幕刷新信号之后，mFrameCallback 又继续做了什么：

```java
// AnimationHandler
private final Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            // 处理动画相关工作
            doAnimationFrame(getProvider().getFrameTime());
            if (mAnimationCallbacks.size() > 0) {
                // 继续向底层注册下一个屏幕刷新事件,因为动画是持续的过程,每一帧都会处理一个动画进度,需要在动画结束前注册监听下一个信号事件
                getProvider().postFrameCallback(this);
            }
        }
    };
```

  当第一个属性动画调用了 `start()` 时，由于 mAnimationCallbacks 列表此时大小为 0，所以直接由 `addAnimationFrameCallback()` 方法内部间接的向底层注册下一个屏幕刷新信号事件，然后将该动画加入到列表里。而当接收到屏幕刷新信号时，mFrameCallback 的 `doFrame()` 会被回调，该方法内部做了两件事，一是去处理当前帧的动画，二则是根据列表的大小是否不为 0 来决定继续向底层注册监听下一个屏幕刷新信号事件，如此反复，直至列表大小为 0。

  接下来跟着`doAnimationFrame()`看看属性动画是怎么执行的

```java
// AnimationHandler
private void doAnimationFrame(long frameTime) {
        long currentTime = SystemClock.uptimeMillis();
        final int size = mAnimationCallbacks.size();
    // 循环遍历列表,取出每一个ValueAnimator
        for (int i = 0; i < size; i++) {
            // ValueAnimator实现了AnimationFrameCallback接口,并且在调用Start()方法后通过AnimationHandler将this缓存到这个列表里
            final AnimationFrameCallback callback = mAnimationCallbacks.get(i);
            if (callback == null) {
                continue;
            }
            // 判断是否到了执行时间
            if (isCallbackDue(callback, currentTime)) {
                // 处理动画逻辑
                callback.doAnimationFrame(frameTime);
                if (mCommitCallbacks.contains(callback)) {
                    getProvider().postCommitCallback(new Runnable() {
                        @Override
                        public void run() {
                            commitAnimationFrame(callback, getProvider().getFrameTime());
                        }
                    });
                }
            }
        }
        // 处理已经结束的动画,清理列表
        cleanUpList();
    }
```

我们先看看`cleanUpList()`

```java
// AnimationHandler
private void cleanUpList() {
        if (mListDirty) {
            for (int i = mAnimationCallbacks.size() - 1; i >= 0; i--) {
                // 就是把null的对象移除掉
                if (mAnimationCallbacks.get(i) == null) {
                    mAnimationCallbacks.remove(i);
                }
            }
            mListDirty = false;
        }
    }
```

  再看看处理动画逻辑`doAnimationFrame()`

```java
// ValueAnimator 
public final boolean doAnimationFrame(long frameTime) {
        if (mStartTime < 0) {
            mStartTime = mReversing
                    ? frameTime
                    : frameTime + (long) (mStartDelay * resolveDurationScale());
        }

        // Handle pause/resume
        if (mPaused) {
            mPauseTime = frameTime;
            removeAnimationCallback();
            return false;
        } else if (mResumed) {
            mResumed = false;
            if (mPauseTime > 0) {
                mStartTime += (frameTime - mPauseTime);
            }
        }

        if (!mRunning) {
            if (mStartTime > frameTime && mSeekFraction == -1) {
                return false;
            } else {
                mRunning = true;
                startAnimation();
            }
        }

        if (mLastFrameTime < 0) {
            // mSeekFraction从某个位置开始
            if (mSeekFraction >= 0) {
                long seekTime = (long) (getScaledDuration() * mSeekFraction);
                // mStartTime 第一帧的时间戳
                mStartTime = frameTime - seekTime;
                mSeekFraction = -1;
            }
            mStartTimeCommitted = false; // allow start time to be compensated for jank
        }
        mLastFrameTime = frameTime;
 
        final long currentTime = Math.max(frameTime, mStartTime);
        // 根据当前时间计算帧的动画进度(核心)
        boolean finished = animateBasedOnTime(currentTime);

        if (finished) {
            // 将当前动画从mAniamtionCallbacks 中移除
            endAnimation();
        }
        return finished;
    }
```

  先分析`endAnimation()`

```java
// ValueAnimator
private void endAnimation() {
        if (mAnimationEndRequested) {
            return;
        }
        // 调用AniamtionHandler 的removeCallback()
        removeAnimationCallback();

        mAnimationEndRequested = true;
        mPaused = false;
        boolean notify = (mStarted || mRunning) && mListeners != null;
        if (notify && !mRunning) {
            notifyStartListeners();
        }
        mRunning = false;
        mStarted = false;
        mStartListenersCalled = false;
        mLastFrameTime = -1;
        mFirstFrameTime = -1;
        mStartTime = -1;
        if (notify && mListeners != null) {
            ArrayList<AnimatorListener> tmpListeners =
                    (ArrayList<AnimatorListener>) mListeners.clone();
            int numListeners = tmpListeners.size();
            for (int i = 0; i < numListeners; ++i) {
                // 通知动画结束
                tmpListeners.get(i).onAnimationEnd(this, mReversing);
            }
        }
        // mReversing needs to be reset *after* notifying the listeners for the end callbacks.
        mReversing = false;
        if (Trace.isTagEnabled(Trace.TRACE_TAG_VIEW)) {
            Trace.asyncTraceEnd(Trace.TRACE_TAG_VIEW, getNameForTrace(),
                    System.identityHashCode(this));
        }
    }


private void removeAnimationCallback() {
        if (!mSelfPulse) {
            return;
        }
        getAnimationHandler().removeCallback(this);
    }

// AnimarionHandler
public void removeCallback(AnimationFrameCallback callback) {
        mCommitCallbacks.remove(callback);
        mDelayedCallbackStartTime.remove(callback);
        int id = mAnimationCallbacks.indexOf(callback);
        if (id >= 0) {
            // 把结束的动画赋值为空
            mAnimationCallbacks.set(id, null);
            mListDirty = true;
        }
    }
```

  如果动画结束，那么它会将其自身在 AnimationCallbacks 列表里的引用赋值为 null，然后移出列表的工作就交由 AnimationHandler 去做。

  接下来看看处理动画第一帧的工作

```java
 if (mStartTime < 0) {
            mStartTime = mReversing
                    ? frameTime
                    : frameTime + (long) (mStartDelay * resolveDurationScale());
        }
if (mLastFrameTime < 0) {
            // mSeekFraction从某个位置开始
            if (mSeekFraction >= 0) {
                long seekTime = (long) (getScaledDuration() * mSeekFraction);
                // mStartTime 第一帧的时间戳
                mStartTime = frameTime - seekTime;
                mSeekFraction = -1;
            }
            mStartTimeCommitted = false; // allow start time to be compensated for jank
        }
```

  记录动画第一帧的时间了，mStartTime 变量就是表示第一帧的时间戳， mSeekFraction 变量，可以任意选择从某个进度开始播放。可以通过 `setCurrentPlayTime()`设置。

小结:

- ValueAnimator 属性动画调用了 start() 之后，会先去进行一些初始化工作，包括变量的初始化、通知动画开始事件；

- 然后通过 AnimationHandler 将其自身 this 添加到 mAnimationCallbacks 队列里，AnimationHandller 是一个单例类，为所有的属性动画服务，列表里存放着所有正在进行或准备开始的属性动画；

- 如果当前存在要运行的动画，那么 AnimationHandler 会去通过 Choreographer 向底层注册监听下一个屏幕刷新信号，当接收到信号时，它的 mFrameCallback 会开始进行工作，工作的内容包括遍历列表来分别处理每个属性动画在当前帧的行为，处理完列表中的所有动画后，如果列表还不为 0，那么它又会通过 Choreographer 再去向底层注册监听下一个屏幕刷新信号事件，如此反复，直至所有的动画都结束。

- AnimationHandler 遍历列表处理动画是在 doAnimationFrame() 中进行，而具体每个动画的处理逻辑则是在各自 ValueAnimator 的 doAnimationFrame() 中进行，各个动画如果处理完自身的工作后发现动画已经结束了，那么会将其在列表中的引用赋值为空，AnimationHandler 最后会去将列表中所有为 null 的都移除掉，来清理资源。

- 每个动画 ValueAnimator 在处理自身的动画行为时，首先，如果当前是动画的第一帧，那么会根据是否有"跳过片头"（setCurrentPlayTime()）来记录当前动画第一帧的时间 mStartTime 应该是什么。

  接下来分析`animateBasedOnTime()`,根据当前时间计算并实现当前帧的动画工作.

```java
// ValueAnimator
boolean animateBasedOnTime(long currentTime) {
        boolean done = false;
        if (mRunning) {
            final long scaledDuration = getScaledDuration();
            // 根据当前时间以及动画第一帧时间和动画持续时长来计算当前进度
            final float fraction = scaledDuration > 0 ?
                    (float)(currentTime - mStartTime) / scaledDuration : 1f;
            final float lastFraction = mOverallFraction;
            final boolean newIteration = (int) fraction > (int) lastFraction;
            // 根据mRepeatCount处理动画是否重新开始
            final boolean lastIterationFinished = (fraction >= mRepeatCount + 1) &&
                    (mRepeatCount != INFINITE);
            if (scaledDuration == 0) {
                // 0 duration animator, ignore the repeat count and skip to the end
                done = true;
            } else if (newIteration && !lastIterationFinished) {
                // Time to repeat
                if (mListeners != null) {
                    int numListeners = mListeners.size();
                    for (int i = 0; i < numListeners; ++i) {
                        mListeners.get(i).onAnimationRepeat(this);
                    }
                }
            } else if (lastIterationFinished) {
                done = true;
            }
            // 确保动画进度取值在0-1之间,(因为属性动画有setRepeatCount()方法,会导致进度超过1.
            mOverallFraction = clampFraction(fraction);
            float currentIterationFraction = getCurrentIterationFraction(
                    mOverallFraction, mReversing);
            // 应用动画
            animateValue(currentIterationFraction);
        }
        return done;
    }
```

进入`animateValue()`方法

```java
// ValueAnimator
void animateValue(float fraction) {
        // 根据插值器计算当前真正的动画进度
        fraction = mInterpolator.getInterpolation(fraction);
        mCurrentFraction = fraction;
        int numValues = mValues.length;
        for (int i = 0; i < numValues; ++i) {
            // 根据动画进度计算出实际的数值
            // mValues 是 PropertyValuesHolder 类型数组
            mValues[i].calculateValue(fraction);
        }
        if (mUpdateListeners != null) {
            int numListeners = mUpdateListeners.size();
            for (int i = 0; i < numListeners; ++i) {
                // 通知动画进度的回调
                mUpdateListeners.get(i).onAnimationUpdate(this);
            }
        }
    }
```

  `PropertyValuesHolder`就是一个存放属性和值的容器,而每次动画的过程中都会从这个容器中取值或者设置值  

看看`calculateValue()`

```java
 // PropertyValuesHolder
 void calculateValue(float fraction) {
        // 通过KeyFrames计算
        Object value = mKeyframes.getValue(fraction);
        mAnimatedValue = mConverter == null ? value : mConverter.convert(value);
    }

// KeyFrames 是一个接口,找一下他的实现类
public interface Keyframes extends Cloneable {
    Object getValue(float fraction);
}

// PropertyValuesHolder
public void setFloatValues(float... values) {
        mValueType = float.class;
        mKeyframes = KeyframeSet.ofFloat(values);
    }
```



其实 `ValueAnimator.ofInt()` 内部会根据相应的方法来创建 mKeyframes 对象，也就是说，在实例化属性动画时，这些 mKeyframes 也顺便被实例化了。

<a name="KeyframeSet的ofInt()"></a>

```java
// KeyframeSet
public static KeyframeSet ofInt(int... values) {
        int numKeyframes = values.length;
        IntKeyframe keyframes[] = new IntKeyframe[Math.max(numKeyframes,2)];
        if (numKeyframes == 1) {
            keyframes[0] = (IntKeyframe) Keyframe.ofInt(0f);
            keyframes[1] = (IntKeyframe) Keyframe.ofInt(1f, values[0]);
        } else {
            keyframes[0] = (IntKeyframe) Keyframe.ofInt(0f, values[0]);
            for (int i = 1; i < numKeyframes; ++i) {
                // 创建关键帧时,第一个参数是关键帧在整个区间之间的位置,第二个参数就是它的值是多少
                keyframes[i] =
                        (IntKeyframe) Keyframe.ofInt((float) i / (numKeyframes - 1), values[i]);
            }
        }
        // 创建了IntKeyframeSet 对象
        return new IntKeyframeSet(keyframes);
    }
```

  动画在处理当前帧的工作时，会去计算当前帧的动画进度，然后根据这个 0-1 区间的进度，映射到我们需要的数值，而这个映射之后的数值就是通过 mKeyframes 的 `getValue()` 里取到的，mKeyframes 是一个 KeyframeSet 对象，在创建属性动画时也顺带被创建了，而创建属性动画时，我们会传入一个我们想要的数值，如 `ValueAnimator.ofInt(100)` 就表示我们想要的动画变化范围是 0-100，那么这个 100 在内部也会被传给 `KeyframeSet.ofInt(100)`，然后就是进入到上面代码块里的创建工作了。

  `KeyframeSet.ofInt()` 最后是创建了一个 IntKeyframeSet 对象,我们看看这个类的`getValue()`是怎么实现的

```java
// IntKeyframeSet
 @Override
    public Object getValue(float fraction) {
        return getIntValue(fraction);
    }


@Override
    public int getIntValue(float fraction) {
        // 处理第一帧
        if (fraction <= 0f) {
            // 获取第一帧和第二帧
            final IntKeyframe prevKeyframe = (IntKeyframe) mKeyframes.get(0);
            final IntKeyframe nextKeyframe = (IntKeyframe) mKeyframes.get(1);
            int prevValue = prevKeyframe.getIntValue();
            int nextValue = nextKeyframe.getIntValue();
            float prevFraction = prevKeyframe.getFraction();
            float nextFraction = nextKeyframe.getFraction();
            final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            // 将动画进度转换成第一帧和第二帧之间的进度
            float intervalFraction = (fraction - prevFraction) / (nextFraction - prevFraction);
            return mEvaluator == null ?
                    prevValue + (int)(intervalFraction * (nextValue - prevValue)) :
                    ((Number)mEvaluator.evaluate(intervalFraction, prevValue, nextValue)).
                            intValue();
        }
        // 处理最后一帧
        else if (fraction >= 1f) {
            // 取出倒数第一帧跟倒数第二帧的信息
            final IntKeyframe prevKeyframe = (IntKeyframe) mKeyframes.get(mNumKeyframes - 2);
            final IntKeyframe nextKeyframe = (IntKeyframe) mKeyframes.get(mNumKeyframes - 1);
            int prevValue = prevKeyframe.getIntValue();
            int nextValue = nextKeyframe.getIntValue();
            float prevFraction = prevKeyframe.getFraction();
            float nextFraction = nextKeyframe.getFraction();
            final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            // 将进度换算到这两针之间的进度
            float intervalFraction = (fraction - prevFraction) / (nextFraction - prevFraction);
            return mEvaluator == null ?
                    prevValue + (int)(intervalFraction * (nextValue - prevValue)) :
                    ((Number)mEvaluator.evaluate(intervalFraction, prevValue, nextValue)).intValue();
        }
        // 处理中间帧
        IntKeyframe prevKeyframe = (IntKeyframe) mKeyframes.get(0);
        // 遍历每一帧,判断当前的动画进度和这一帧的位置
        for (int i = 1; i < mNumKeyframes; ++i) {
            IntKeyframe nextKeyframe = (IntKeyframe) mKeyframes.get(i);
            if (fraction < nextKeyframe.getFraction()) {
                final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
                float intervalFraction = (fraction - prevKeyframe.getFraction()) /
                    (nextKeyframe.getFraction() - prevKeyframe.getFraction());
                int prevValue = prevKeyframe.getIntValue();
                int nextValue = nextKeyframe.getIntValue();
                // Apply interpolator on the proportional duration.
                if (interpolator != null) {
                    intervalFraction = interpolator.getInterpolation(intervalFraction);
                }
                return mEvaluator == null ?
                        prevValue + (int)(intervalFraction * (nextValue - prevValue)) :
                        ((Number)mEvaluator.evaluate(intervalFraction, prevValue, nextValue)).
                                intValue();
            }
            prevKeyframe = nextKeyframe;
        }
        // shouldn't get here
        return ((Number)mKeyframes.get(mNumKeyframes - 1).getValue()).intValue();
    }
```

  当关键帧只有两帧时，比如 `ValueAnimator.ofInt(100)`， 内部其实就是只创建了两个关键帧，一个是起点 0，一个是结束点 100。那么，在这种只有两帧的情况下，将 0-1 的动画进度值转换成我们需要的 0-100 区间内的值，系统的处理很简单，如果没有设置估值器，那么就直接是按比例来转换，比如进度为 0.5，那按比例转换就是 (100 - 0) * 0.5 = 50。

  在处理第一帧的工作时，只需要将第二帧当成是最后一帧，那么第一帧和第二帧这样也就可以看成是只有两帧的场景了吧。但是参数 fraction 动画进度是以实际第一帧到最后一帧计算出来的，所以需要先对它进行转换，换算出它在第一帧到第二帧之间的进度，接下去的逻辑也就跟处理两帧时的逻辑是一样的了。同理, 在处理最后一帧时，只需要取出倒数第一帧跟倒数第二帧的信息，然后将进度换算到这两针之间的进度。

  处理中间帧比较复杂,系统从第一帧开始，按顺序遍历每一帧，然后去判断当前的动画进度跟这一帧保存的位置信息来找出当前进度是否就是落在某两个关键帧之间。然后按照两帧的情况处理

  小结:

- 当接收到屏幕刷新信号后，AnimationHandler 会去遍历列表，将所有待执行的属性动画都取出来去计算当前帧的动画行为。
- 每个动画在处理当前帧的动画逻辑时，首先会先根据当前时间和动画第一帧时间以及动画的持续时长来初步计算出当前帧时动画所处的进度，然后会将这个进度值等价转换到 0-1 区间之内。
- 接着，插值器会将这个经过初步计算之后的进度值根据设定的规则计算出实际的动画进度值，取值也是在 0-1 区间内。
- 计算出当前帧动画的实际进度之后，会将这个进度值交给关键帧机制，来换算出我们需要的值，比如 ValueAnimator.ofInt(0, 100) 表示我们需要的值变化范围是从 0-100，那么插值器计算出的进度值是 0-1 之间的，接下去就需要借助关键帧机制来映射到 0-100 之间。
- 关键帧的数量是由 ValueAnimator.ofInt(0, 1, 2, 3) 参数的数量来决定的，比如这个就有四个关键帧，第一帧和最后一帧是必须的，所以最少会有两个关键帧，如果参数只有一个，那么第一帧默认为 0，最后一帧就是参数的值。当调用了这个 ofInt() 方法时，关键帧组也就被创建了。
- 当只有两个关键帧时，映射的规则是，如果没有设置估值器，那么就等比例映射，比如动画进度为 0.5，需要的值变化区间是 0-100，那么等比例映射后的值就是 50，那么我们在 onAnimationUpdate 的回调中通过 animation.getAnimatedValue() 获取到的值 50 就是这么来的。
- 当关键帧超过两个时，需要先找到当前动画进度是落于哪两个关键帧之间，然后将这个进度值先映射到这两个关键帧之间的取值，接着就可以将这两个关键帧看成是第一帧和最后一帧，那么就可以按照只有两个关键帧的情况下的映射规则来进行计算了。
- 而进度值映射到两个关键帧之间的取值，这就需要知道每个关键帧在整个关键帧组中的位置信息，或者说权重。而这个位置信息是在创建每个关键帧时就传进来的。onInt() 的规则是所有关键帧按等比例来分配权重。

到这里我们就分析完`ValueAnimator`了

我们再来看一下ValueAnimator的时序图

![ValueAnimator原理时序图](https://s2.loli.net/2022/07/29/A6eql3FxIhgyS8H.png)

[ValueAnimation时序图 -查看大图](https://www.processon.com/view/link/62e1d8d6f346fb0760d091d5)

#### ObjectAnimator源码解读



  我们先从`ObjectAnimator.ofInt()`开始

```java
// ObjectAnimator
public static ObjectAnimator ofInt(Object target, String propertyName, int... values) {
        // 创建ObjectAnimator对象,设置属性动画的目标target和作用于target的属性名propertyName
        ObjectAnimator anim = new ObjectAnimator(target, propertyName);
        anim.setIntValues(values);
        return anim;
    }
```

  看一下`ObjectAnimator`的构造函数

```java
// ObjectAnimator
private ObjectAnimator(Object target, String propertyName) {
        // 设置目标View
        setTarget(target);
        // 设置属性名称
        setPropertyName(propertyName);
    }


@Override
    public void setTarget(@Nullable Object target) {
        final Object oldTarget = getTarget();
        // 当oldTarget不等于target，且已经调用了start()方法使得mStarted标志位为true时,先取消掉动画
        if (oldTarget != target) {
            if (isStarted()) {
                cancel();
            }
            // 这是一个软引用,这样ObjectAnimator就不会持有View的引用，不会影响Activity的正常回收，从而不会引起Activity内存泄漏。
            mTarget = target == null ? null : new WeakReference<Object>(target);
            // 记录未初始化,ValueAnimator的初始化标志位
            mInitialized = false;
        }
    }


public void setPropertyName(@NonNull String propertyName) {
        // mValues是一个数组，用于保存PropertyValuesHolder
        if (mValues != null) {
            PropertyValuesHolder valuesHolder = mValues[0];
            String oldName = valuesHolder.getPropertyName();
            // 更新第一个PropertyValuesHolder的PropertyName
            valuesHolder.setPropertyName(propertyName);
            // mValues和mValuesMap都是存储PropertyValueHolder的属性
            mValuesMap.remove(oldName);
            mValuesMap.put(propertyName, valuesHolder);
        }
        mPropertyName = propertyName;
        // 记录尚未初始化,ValueAnimator的标志位
        mInitialized = false;
    }
```

回到`ObjectAnimator.OfInt()`方法,查看`ObjectAnimator.setIntValues()`方法

```java
// ObjectAnimator
 @Override
    public void setIntValues(int... values) {
        // 第一次调用时，values为null
        if (mValues == null || mValues.length == 0) {
            // 我们设置了mPropertyName，在这里mProperty为null
            if (mProperty != null) {
                setValues(PropertyValuesHolder.ofInt(mProperty, values));
            } else {
                setValues(PropertyValuesHolder.ofInt(mPropertyName, values));
            }
        } else {
            super.setIntValues(values);
        }
    }
```

  进入`PropertyValuesHolder.ofInt(mPropertyName, values)`方法

```java
// PropertyValuesHolder
public static PropertyValuesHolder ofInt(String propertyName, int... values) {
        return new IntPropertyValuesHolder(propertyName, values);
    }


public IntPropertyValuesHolder(String propertyName, int... values) {
            super(propertyName);
            // 调用IntPropertyValuesHolder.setIntValues方法
            setIntValues(values);
        }

@Override
        public void setIntValues(int... values) {
            // 调用PropertyValuesHolder.setIntValues方法
            super.setIntValues(values);
            mIntKeyframes = (Keyframes.IntKeyframes) mKeyframes;
        }


public void setIntValues(int... values) {
        // 记录mValueType值，便于使用反射方式遍历获取目标target对应的set和get方法
        mValueType = int.class;
    }
```

`ObjectAnimator.ofFloat`的过程就结束了，

小结:

  ofInt()会创建`ObjecctAnimator`对象,并设置动画目标target和属性名propertyName,然后调用`PropertyValuesHolder.ofInt()`进行初始化

下面我们来看ObjectAnimator 的 `start()` 方法

```java
// ObjectAnimator
   @Override
    public void start() {
        // 取消当前的动画
        AnimationHandler.getInstance().autoCancelBasedOn(this);
        if (DBG) {
            Log.d(LOG_TAG, "Anim target, duration: " + getTarget() + ", " + getDuration());
            for (int i = 0; i < mValues.length; ++i) {
                PropertyValuesHolder pvh = mValues[i];
                Log.d(LOG_TAG, "   Values[" + i + "]: " +
                    pvh.getPropertyName() + ", " + pvh.mKeyframes.getValue(0) + ", " +
                    pvh.mKeyframes.getValue(1));
            }
        }
        // 调用了ValueAnimator的start()
        super.start();
    }
```

  我们跟进`ValueAnimator.start()`->`ValueAnimator.setCurrent()`->`initAnimator`

我们先看看`ObjectAnimator.initAnimation()`方法

```java
// ObjectAnimator
@Override
    void initAnimation() {
        // 第一次执行时，mInitialized为false,初始化后该标志位置为true
        if (!mInitialized) {
            final Object target = getTarget();
            if (target != null) {
                final int numValues = mValues.length;
                for (int i = 0; i < numValues; ++i) {
                    // 执行PropertyValuesHolder的setupSetterAndGetter()方法
                    mValues[i].setupSetterAndGetter(target);
                }
            }
            super.initAnimation();
        }
    }
```

 分析`PropertyValuesHolder.setupSetterAndGetter()`,主要是初始化反射方法mSetter和mGetter

```java
// PropertyValuesHolder
 void setupSetterAndGetter(Object target) {
     // 如果我们设置了mPropertyName,mProperty为null
        if (mProperty != null) {
            try {
                Object testValue = null;
                List<Keyframe> keyframes = mKeyframes.getKeyframes();
                int keyframeCount = keyframes == null ? 0 : keyframes.size();
                for (int i = 0; i < keyframeCount; i++) {
                    Keyframe kf = keyframes.get(i);
                    if (!kf.hasValue() || kf.valueWasSetOnStart()) {
                        if (testValue == null) {
                            testValue = convertBack(mProperty.get(target));
                        }
                        kf.setValue(testValue);
                        kf.setValueWasSetOnStart(true);
                    }
                }
                return;
            } catch (ClassCastException e) {
                Log.w("PropertyValuesHolder","No such property (" + mProperty.getName() +
                        ") on target object " + target + ". Trying reflection instead");
                mProperty = null;
            }
        }
        // We can't just say 'else' here because the catch statement sets mProperty to null.
         // mProperty为空，判断get和set方法是否存在
        if (mProperty == null) {
            Class targetClass = target.getClass();
            if (mSetter == null) {
                // 查找目标属性的set方法，初始化mSetter方法
                setupSetter(targetClass);
            }
            List<Keyframe> keyframes = mKeyframes.getKeyframes();
            int keyframeCount = keyframes == null ? 0 : keyframes.size();
            // 遍历关键帧集合
            for (int i = 0; i < keyframeCount; i++) {
                Keyframe kf = keyframes.get(i);
                if (!kf.hasValue() || kf.valueWasSetOnStart()) {
                    if (mGetter == null) {
                        // 查找目标属性的get方法，初始化mGetter方法
                        setupGetter(targetClass);
                        // mGetter为null时，直接return
                        if (mGetter == null) {
                            // Already logged the error - just return to avoid NPE
                            return;
                        }
                    }
                    try {
                        // 通过mGetter反射获取属性值
                        Object value = convertBack(mGetter.invoke(target));
                        // 初始化关键帧Keyframe的mValue值
                        kf.setValue(value);
                        // 设置Keyframe标志位为true，表示已经初始化mValue
                        kf.setValueWasSetOnStart(true);
                    } catch (InvocationTargetException e) {
                        Log.e("PropertyValuesHolder", e.toString());
                    } catch (IllegalAccessException e) {
                        Log.e("PropertyValuesHolder", e.toString());
                    }
                }
            }
        }
    }
```

`  setupSetter`和`setupGetter`方法都调用了`setupSetterOrGetter()`接着分析`setupSetterOrGetter()`方法

```java
// PropertyValuesHolder
void setupSetter(Class targetClass) {
        Class<?> propertyType = mConverter == null ? mValueType : mConverter.getTargetType();
        mSetter = setupSetterOrGetter(targetClass, sSetterPropertyMap, "set", propertyType);
    }

private void setupGetter(Class targetClass) {
        mGetter = setupSetterOrGetter(targetClass, sGetterPropertyMap, "get", null);
    }



// 参数prefix值为“set”或者“get”,在这里valueType为int.class
    private Method setupSetterOrGetter(Class targetClass,
            HashMap<Class, HashMap<String, Method>> propertyMapMap,
            String prefix, Class valueType) {
        Method setterOrGetter = null;
        // 进行同步判断
        synchronized(propertyMapMap) {
            // 根据targetClass获取HashMap，这个propertyMap是以mPropertyName为key，set或者get方法作为value
            HashMap<String, Method> propertyMap = propertyMapMap.get(targetClass);
            boolean wasInMap = false;
            if (propertyMap != null) {
                wasInMap = propertyMap.containsKey(mPropertyName);
                if (wasInMap) {
                    setterOrGetter = propertyMap.get(mPropertyName);
                }
            }
            // 第一次初始化，wasInMap为false
            if (!wasInMap) {
                // 初始化setterOrGetter
                setterOrGetter = getPropertyFunction(targetClass, prefix, valueType);
                if (propertyMap == null) {
                    propertyMap = new HashMap<String, Method>();
                    propertyMapMap.put(targetClass, propertyMap);
                }
                propertyMap.put(mPropertyName, setterOrGetter);
            }
        }
        return setterOrGetter;
    }
```

在`setupSetterOrGetter`方法中调用到了`getPropertyFunction`函数来初始化mSetter或者mGetter参数

```java
// PropertyValuesHolder
private Method getPropertyFunction(Class targetClass, String prefix, Class valueType) {
        // TODO: faster implementation...
        Method returnVal = null;
        // 通过prefix和mPropertyName拼接出方法名，如setAlpha或者getAlpha
        String methodName = getMethodName(prefix, mPropertyName);
        Class args[] = null;
        if (valueType == null) {
            try {
                returnVal = targetClass.getMethod(methodName, args);
            } catch (NoSuchMethodException e) {
                // Swallow the error, log it later
            }
        } else {
            args = new Class[1];
            Class typeVariants[];
            if (valueType.equals(Float.class)) {
                typeVariants = FLOAT_VARIANTS;
            } else if (valueType.equals(Integer.class)) {
                typeVariants = INTEGER_VARIANTS;
            } else if (valueType.equals(Double.class)) {
                typeVariants = DOUBLE_VARIANTS;
            } else {
                typeVariants = new Class[1];
                typeVariants[0] = valueType;
            }
            // FLOAT_VARIANTS,遍历含有float.class、Float.class、double.class、Double.class等参数的方法
            // 只要是相关的基本类型，都会遍历反射查找set或者get方法
            for (Class typeVariant : typeVariants) {
                args[0] = typeVariant;
                try {
                    // 反射获取方法，成功则直接返回
                    returnVal = targetClass.getMethod(methodName, args);
                    if (mConverter == null) {
                        // change the value type to suit
                        mValueType = typeVariant;
                    }
                    return returnVal;
                } catch (NoSuchMethodException e) {
                    // Swallow the error and keep trying other variants
                }
            }
            // If we got here, then no appropriate function was found
        }

        if (returnVal == null) {
            Log.w("PropertyValuesHolder", "Method " +
                    getMethodName(prefix, mPropertyName) + "() with type " + valueType +
                    " not found on target class " + targetClass);
        }

        return returnVal;
    }
```

  到这里ObjectAnimator的`initAnimation()`方法已经完成,接下来我们回到`ValueAnimator.setCurrentFraction(float fraction)`方法，最后调用了`ObjectAnimator.animateValue(float fraction)`

```java
// ObjectAnimator
@Override
    void animateValue(float fraction) {
        final Object target = getTarget();
        // mTarget是一个软引用，判断target是否已经被回收
        if (mTarget != null && target == null) {
            // We lost the target reference, cancel and clean up. Note: we allow null target if the
            /// target has never been set.
            cancel();
            return;
        }

        // 这里调用父类ValueAnimator的animateValue来计算数值
        super.animateValue(fraction);
        int numValues = mValues.length;
        for (int i = 0; i < numValues; ++i) {
            // 反射修改每一个属性值，这里修改完这一轮动画就结束了
            mValues[i].setAnimatedValue(target);
        }
    }
```

  我们看看`PropertyValuesHolder.setAnimatedValue()`

```java
// IntPropertyValuesHolder
@Override
        void setAnimatedValue(Object target) {
            // 我们传进来的是mPropertyName
            if (mIntProperty != null) {
                mIntProperty.setValue(target, mIntAnimatedValue);
                return;
            }
            if (mProperty != null) {
                mProperty.set(target, mIntAnimatedValue);
                return;
            }
            if (mJniSetter != 0) {
                nCallIntMethod(target, mJniSetter, mIntAnimatedValue);
                return;
            }
            // 终于到了，反射修改属性值就在这里执行的
            if (mSetter != null) {
                try {
                    mTmpValueArray[0] = mIntAnimatedValue;
                    mSetter.invoke(target, mTmpValueArray);
                } catch (InvocationTargetException e) {
                    Log.e("PropertyValuesHolder", e.toString());
                } catch (IllegalAccessException e) {
                    Log.e("PropertyValuesHolder", e.toString());
                }
            }
        }
```

  到这里我们把`setCurrentPlayTime()`分析完,`ObjectAnimator`也就分析完了,其他的细节和`ValueAnimator`一样.





## 转场动画

  看完了属性动画后,我们再来看看转场动画

### Transition

在 Android 5.0 之后，我们可以使用 Transition 为我们带来的转场动画。

| Explode                                                      | Slide                                                        | Fade                                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| 从中心移入或移出                                             | 从边缘移入或移出                                             | 调整透明度产生渐变                                           |
| <img src="D:\OneDrive\桌面\动画可见\gif\Transition-Explode.gif" alt="Transition-Explode" style="zoom:25%;" /> | <img src="D:\OneDrive\桌面\动画可见\gif\Transition-Slide.gif" alt="Transition-Slide" style="zoom:25%;" /> | <img src="D:\OneDrive\桌面\动画可见\gif\Transition-Fade.gif" alt="Transition-Fade" style="zoom:25%;" /> |



这三个类都继承于 Transition ，所有有一些属性都是共同的。 常用属性如下：

```java
// 设置动画的时间。
transition.setDuration();
// 设置插值器
transition.setInterpolator();
// 设置动画开始时间，延迟n毫秒播放。
transition.setStartDelay();
// 设置动画的运行路径
transition.setPathMotion();
// 改变动画 出现/消失 的模式。Visibility.MODE_IN:进入；Visibility.MODE_OUT：退出。
transition.setMode();
// 设置动画的监听事件
transition.addListener()
```

插值器,与前面一致,[常见的插值器](#常用的插值器)

#### Transition的时机

```java
//总共有四个过程
//退出 -> 进入  -> 返回   -> 重新进入
//Exit -> Enter -> Return -> Reenter
//界面1
android:windowExitTransition      //启动新 Activity ，此页面退出的动画
android:windowReenterTransition   //重新进入的动画。即第二次进入，可以和首次进入不一样。
//界面2
android:windowEnterTransition     //首次进入显示的动画
android:windowReturnTransition    //finishAfterTransition()退出时，此页面退出的动画.
```

- 首先打开页面A ：页面A -> Enter 首次进入
- 从 A 打开 B ：页面A -> Exit 退出 页面B -> Enter 首次进入
- 从 B 返回 A ：页面B -> Return 返回 页面A -> Reenter 重新进入

| 方法                                 | 作用                                                         |
| :----------------------------------- | :----------------------------------------------------------- |
| windowContentTransitions             | 允许使用transitions                                          |
| windowAllowEnterTransitionOverlap    | 是否覆盖执行，其实可以理解成前后两个页面是同步执行还是顺序执行 |
| windowAllowReturnTransitionOverlap   | 与上面相同。即上一个设置了退出动画，这个设置了进入动画，两者是否同时执行。 |
| windowContentTransitionManager       | 引用TransitionManager XML资源，定义不同窗口内容之间的所需转换。 |
| windowEnterTransition                | 首次进入显示的动画                                           |
| windowExitTransition                 | 启动新 Activity ，此页面退出的动画                           |
| windowReenterTransition              | 重新进入的动画。即第二次进入，可以和首次进入不一样。         |
| windowReturnTransition               | 调用 finishAfterTransition() 退出时，此页面退出的动画        |
| windowSharedElementsUseOverlay       | 指示共享元素在转换期间是否应使用叠加层。                     |
| windowSharedElementEnterTransition   | 首次进入显示的动画                                           |
| windowSharedElementExitTransition    | 启动新 Activity ，此页面退出的动画                           |
| windowSharedElementReenterTransition | 重新进入的动画。即第二次进入，可以和首次进入不一样。         |
| windowSharedElementReturnTransition  | 调用 finishAfterTransition() 退出时，此页面退出的动画        |

#### 跳转页面

```kotlin
//页面1 (Activity默认了一个从右到左的Slide动画)
val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)

//页面2可以什么也不写
// 如果定义了 return transition ，将使用定义的动画过渡
        window.returnTransition = Fade()
// 如果没有定义returnTransition，将使用 反转进入 的动画
    finishAfterTransition();
```

**Shared Elements Transition 共享元素**

携带需要共享的 View 进行跳转, 也就是在跳转的参数中，增加了要共享的 View控件。

1. 先在 layout 布局里面给需要共享的元素加上transitionName：

   ```xml
   android:transitionName="image"
   ```

2. 然后开始设置跳转页面，代码如下：

   ```kotlin
   val intent = Intent(this, MainActivity2::class.java)
           val transitionActivityOptions =
               ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair(textView, "translation1"))
           startActivity(intent, transitionActivityOptions.toBundle())
   ```

和普通Transition区别是增加了`Pair.create()`

```java
   Pair.create（
      View view,      // 本页面要共享的 View
      String resId    // 设置的transitionName
   ）
```

效果图:

<img src="D:\OneDrive\桌面\动画可见\gif\Activity-共享元素.gif" alt="Activity-共享元素" style="zoom:25%;" />

  fragment与fragment之间地动画

```kotlin
//在fragment1中
val fragment2 = Fragment2()
            this.exitTransition = Explode()
            fragment2.enterTransition = Slide()
            requireActivity().supportFragmentManager.beginTransaction()
                .addSharedElement(textView, "translation2")
                .replace(R.id.constraintLayout3, fragment2).addToBackStack(null).commit()

//在fragment2中
sharedElementEnterTransition = TransitionInflater.from(getContext())
            .inflateTransition(android.R.transition.move);
```

  可以设置一些界面切换的效果,和Transition用法一致

效果图:

<img src="https://s2.loli.net/2022/07/27/wE2KrFdqeT8z4BO.gif" alt="fragment-共享元素" style="zoom:25%;" />

  注意:有时从`RecyclerView`界面进入到详情页，由于详情页加载延迟，可能出现没有效果。例如`ImageView`从网络加载图片，可能A界面到B界面没效果，B回到A界面有效果。

例如:

<img src="D:\OneDrive\桌面\动画可见\gif\Shared_Transition_no_Postpone.gif" alt="Shared_Transition_no_Postpone" style="zoom:25%;" />

  解决步骤：

- 在`setContentView`后添加代码`postponeEnterTransition()`，延迟加载过渡动画。

- 在共享元素视图加载完毕，或者图片加载完毕后调用代码`startPostponedEnterTransition()`,开始加载过渡动画。

效果图:

<img src="D:\OneDrive\桌面\动画可见\gif\Shared_transition_no_exit_retreen.gif" alt="Shared_transition_no_exit_retreen" style="zoom:25%;" />

当Activity A 调用 Activity B ，发生的事件流如下：

1）Activity A调用startActivity()，ActivityB被创建，测量，同时初始化为半透明的窗口和透明的背景颜色。

2）framework重新分配每个共享元素在B中的位置与大小，使其跟A中一模一样。之后，B的进入变换（enter transition）捕获到共享元素在B中的初始状态。

3）framework重新分配每个共享元素在B中的位置与大小，使其跟B中的最终状态一致。之后，B的进入变换（enter transition）捕获到共享元素在B中的结束状态。

4）B的进入变换（enter transition）比较共享元素的初始和结束状态，同时基于前后状态的区别创建一个Animator(属性动画对象)。

5）framework 命令A隐藏其共享元素，动画开始运行。随着动画的进行，framework 逐渐将B的activity窗口显示出来，当动画完成，B的窗口才完全可见。

#### TransitionManager 控制动画

  Activity切换过渡动画指在两个Activity之间，而布局变化过渡动画，是指同个Activity之间View的变化过渡动画。

  具体步骤: 需要我们自己创建起始和结束场景，利用现有的过渡效果来达到两个场景的切换 (起始场景和结束场景需要用相同根元素) 。默认情况下，当前界面就是起始场景。

```xml
<!--第一步创建scene2-->
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/constraintLayout2"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity2">

    <TextView
        android:id="@+id/textView2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Red_Rock"
        android:textColor="@color/purple_200"
        android:textSize="60dp"
        android:textStyle="bold"
        android:transitionName="translation1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <ImageView
        android:id="@+id/ball3"
        android:layout_width="50dp"
        android:layout_height="50dp"
        android:layout_marginTop="128dp"
        android:background="@color/purple_200"
        android:src="@drawable/drawable"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <ImageView
        android:id="@+id/ball4"
        android:layout_width="50dp"
        android:layout_height="50dp"
        android:layout_marginTop="16dp"
        android:background="@color/teal_200"
        android:src="@drawable/drawable"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/ball3" />

    <ImageView
        android:id="@+id/ball5"
        android:layout_width="50dp"
        android:layout_height="50dp"
        android:layout_marginTop="16dp"
        android:background="@color/black"
        android:src="@drawable/drawable"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/ball4" />


    <Button
        android:id="@+id/button11"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="8dp"
        android:text="旋转"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@+id/button12"
        app:layout_constraintStart_toStartOf="parent" />

    <Button
        android:id="@+id/button12"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="8dp"
        android:text="平移"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@+id/button14"
        app:layout_constraintStart_toEndOf="@+id/button11" />

    <Button
        android:id="@+id/button13"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="8dp"
        android:text="缩放"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/button14" />

    <Button
        android:id="@+id/button14"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="8dp"
        android:text="透明"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@+id/button13"
        app:layout_constraintStart_toEndOf="@+id/button12" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

```kotlin
// 第二步调用 Scene.getSceneForLayout() 保存每个Layout；
val scene = Scene.getSceneForLayout(constraintLayout, R.xml.scene2, this)
// 第三步调用 TransitionManager.go(scene1, new ChangeBounds()) 切换。
// 参数1表示结束场景, 参数2表示过渡效果
TransitionManager.go(scene, ChangeBounds())
```

效果图:

<img src="D:\OneDrive\桌面\动画可见\gif\TransitionManager-动画.gif" alt="TransitionManager-动画" style="zoom:25%;" />

**过渡动画**

  系统支持将任何扩展Visibility类的过渡作为进入或退出过渡，内置继承自Visibility的类有`Explode`、`Slide`、`Fade`；支持共享元素过渡的有： 

- changeScroll 为目标视图滑动添加动画效果
- changeBounds 为目标视图布局边界的变化添加动画效果
- changeClipBounds 为目标视图裁剪边界的变化添加动画效果
- changeTransform  为目标视图缩放和旋转方面的变化添加动画效果
- changeImageTransform  为目标图片尺寸和缩放方面的变化添加动画效果 

 默认转场动画会对所有的子View进行遍历加载动画, 但是如果**添加目标**则不会进行遍历所有子View, 或者也可以排除特定View.

对于目标有三个操作:

- 添加(`addTarget`):默认会进行遍历所有的视图加载动画, 但是如果使用了添加就不会遍历所有, 只会让指定的视图进行动画

- 排除(`excludeTarget()`):如果使用排除方法, 依旧会进行遍历视图对象, 不过会排除你指定的视图

- 删除(`removeTarget()`):删除目标是在动画已经遍历视图完成以后还想对目标集合进行变更, 就可以删除指定的视图


  添加/排除和删除目标支持的参数类型: `视图对象(View)`,`过渡名(TransitionNames)`,`字节码(Class)`,`ID`

```java
// 视图动画
Transition addTarget (View target)
// 过渡名
Transition addTarget (String targetName)
// 字节码
Transition addTarget (Class targetType)
// Id
Transition addTarget (int targetId)
```

#### 自定义Translation

  当现有的过渡效果不满足日常需求时，可以通过继承`Transition`，定制自己的动画特效。

子类继承`Transition`类，并重写其三个方法。

```kotlin
class MyTransition : Transition() {
   // 捕获动画的起始值
   override fun captureStartValues(transitionValues: TransitionValues?) {}
   // 捕获动画的结束值
   override fun captureEndValues(transitionValues: TransitionValues?) {}

   override fun createAnimator(
       sceneRoot: ViewGroup?,
       startValues: TransitionValues?,
       endValues: TransitionValues?
   ): Animator {
       return super.createAnimator(sceneRoot, startValues, endValues)
   }
   
}

// 来看看TransitionValues这个类
public class TransitionValues {
    // 捕获的View
    public View view;
    // 存放我们收集的信息
    public final Map<String, Object> values = new ArrayMap<String, Object>();

    final ArrayList<Transition> targetedTransitions = new ArrayList<Transition>();
```

`captureStartValues()`与`captureEndValues()`方法是必须实现的，和结束值，而`createAnimator()`方法，是用来创建自定义的动画。

参数`TransitionValues`可以理解是用来存储View的一些属性值，参数`sceneRoot`为根视图。

先看效果图:

<img src="D:\OneDrive\桌面\动画可见\gif\自定义Transition.gif" alt="自定义Transition" style="zoom:25%;" />

```java
public class MyTransition extends Transition {
    // 收集高度和宽度
    private static final String TOP = "top";
    private static final String HEIGHT = "height";

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        // 拿到我们的目标view
        View view = transitionValues.view;
        Rect rect = new Rect();
        // 拿到它的父布局上下左右的距离
        view.getHitRect(rect);
        // 保存到values
        transitionValues.values.put(TOP, rect.top);
        transitionValues.values.put(HEIGHT, view.getHeight());
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        // 和上面同理
        transitionValues.values.put(TOP, 0);
        transitionValues.values.put(HEIGHT, transitionValues.view.getHeight());
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        // 初始化
        final View endView = endValues.view;
        final int startTop = (int) startValues.values.get(TOP);
        final int startHeight = (int) startValues.values.get(HEIGHT);
        final int endTop = (int) endValues.values.get(TOP);
        final int endHeight = (int) endValues.values.get(HEIGHT);
        // 首先把view的高度设置为前一个界面上view的高度是为了防止在移动的过程中view的高度是他自身的高度的.
        endView.setTranslationY(startTop);
        endView.getLayoutParams().height = startHeight;
        endView.requestLayout();
        // 移动动画
        ValueAnimator positionAnimator = ValueAnimator.ofInt(startTop, endTop);
        positionAnimator.setDuration(200);
        positionAnimator.setInterpolator(new LinearInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int current = (int) positionAnimator.getAnimatedValue();
                endView.setTranslationY(current);
            }
        });
        // 展开动画
        ValueAnimator sizeAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        sizeAnimator.setDuration(200);
        sizeAnimator.setInterpolator(new LinearInterpolator());
        sizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int current = (int) valueAnimator.getAnimatedValue();
                endView.getLayoutParams().height = current;
                endView.requestLayout();
            }
        });
        // 动画集合
        AnimatorSet set = new AnimatorSet();
        set.play(sizeAnimator).after(positionAnimator);
        return set;
    }
}
```

  下面是跳转代码

```kotlin
    // 界面1中
	private fun startActivity(view: View, msg: String) {
        val intent = Intent(this, MainActivity5::class.java)
        intent.putExtra("msg", msg)
        val pair1 = Pair.create(view, view.transitionName)
        // 和前面的操作一样
        val compat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1)
        ActivityCompat.startActivity(this, intent, compat.toBundle())
    }
}

// 界面2中
val textView = findViewById<TextView>(R.id.textView5)
        textView.text = intent.getStringExtra("msg")
        val transition = MyTransition()
        // 这个target就是我们设置的transitionName
        transition.addTarget("transition2")
        // 设置成我们自定义的Transition
        window.sharedElementEnterTransition = transition
```

  这里有一个NavigationBar会有闪烁问题,原因是共享元素动画是在整个窗口的view上执行的,解决方法就是将导航栏作为动画的一部分,然后在界面2中延迟动画,有两个方法来延迟动画执行: `postponeEnterTransition()`和`startPostponedEnterTransition()`

```kotlin
// 界面1
val decor = window.decorView
        decor.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                decor.viewTreeObserver.removeOnPreDrawListener(this)
                // 从根布局获得statusBar和navigationBar实例
                statusBar = decor.findViewById<View>(android.R.id.statusBarBackground)
                navigationBar = findViewById<View>(android.R.id.navigationBarBackground)
                return true
            }
        })
    val intent = Intent(this, MainActivity5::class.java)
    intent.putExtra("msg", msg)
    val pair1 = Pair.create(view, view.transitionName)
    // 给statusBar和navigationBar加上动画
    val pair2 = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)
    val pair3 = Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)
    val compat =
        ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1,pair2,pair3 )
    ActivityCompat.startActivity(this, intent, compat.toBundle())

//界面2
val textView = findViewById<TextView>(R.id.textView5)
        textView.text = intent.getStringExtra("msg")
        postponeEnterTransition()
        val decorView = window.decorView
        window.decorView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                decorView.viewTreeObserver.removeOnPreDrawListener(this)
                supportStartPostponedEnterTransition()
                return true
            }
        })
        val transition = MyTransition()
        transition.addTarget("transition2")
        window.sharedElementEnterTransition = transition
```



#### Transition源码

  这个有点多,我就贴个链接,想了解的自己看一下

[Android Transition源码分析 - 掘金](https://juejin.cn/post/6844903488040747015)



### Material Motion 

​    **Material Design YYDS**

```groovy
    implementation 'com.google.android.material:material:1.6.0'
```

  `MaterialContainerTransform` 是一个共享元素动画，但和传统的Android共享元素动画不同，它不是围绕单一的共享内容(如图像)设计的。这里的共享元素指的是开始视图或ViewGroup容器将其大小和形状转换为结束视图或ViewGroup容器的大小和形状，可以理解为开始和结束容器视图是容器转换的“共享元素”。并且可以用在 Fragments, Activities 或者 Views.

material-motion-android 是MDC-Android库中的一组过渡模式; 其中主要包含四种过渡模式:

- **ContainerTransform** : 包含 container 的UI元素之间的转换 在两个不同的UI元素之间创建可见连接, 使一个UI元素无缝过渡到另一个UI元素.
- **SharedAxis** : 具有空间或导航关系的UI元素之间的过渡; 在X,Y或Z轴上使用共享变换来加强元素之间的关系.
- **FadeThrough** : 在彼此之间没有密切关系的UI元素之间进行过渡; 使用顺序淡入和淡入, 以及传入元素的比例.
- **Fade** : 用于在屏幕范围内进入或退出的UI元素.

一些常用函数

- **fadeMode**

设置淡入淡出模式，用于将开始视图的内容与结束视图的内容切换。 有四种淡入淡出模式：

`FADE_MODE_IN` ：淡入传入内容，而不更改传出内容的不透明度。 这是默认模式。

`FADE_MODE_OUT` ：淡出输出内容而不更改输入内容的不透明度。

`FADE_MODE_CROSS` ：淡入淡出传入的内容。

`FADE_MODE_THROUGH` ： `FADE_MODE_THROUGH`淡出输出内容并淡入输入内容。

- **fitMode**

共有三个选项：

`FIT_MODE_HEIGHT` ：在缩放动画期间，将传入内容调整为传出内容的高度。

`FIT_MODE_WIDTH` ：在缩放动画过程中，使传入内容适合传出内容的宽度。

`FIT_MODE_AUTO` ：自动使用FIT_MODE_HEIGHT或FIT_MODE_WIDTH。

- **containerColor**

设置变形容器的背景颜色。 此颜色绘制在开始视图和结束视图下方。 当一个或两个都不具有纯色背景时，这非常有用。 这也可以用来设置过渡的颜色，以增强从一种视图到另一种视图的转化。

- **scrimColor**

设置要在drawingView范围内的变形容器下绘制的颜色。

```kotlin
private fun buildContainerTransform() =
        MaterialContainerTransform().apply {
            startView =findViewById(R.id.floatingActionBar1)
            endView =findViewById(R.id.materialCardView1)
            duration = 300
            scrimColor = Color.TRANSPARENT
            containerColor =
                requireContext().themeColor(com.google.android.material.R.attr.colorSurface)
            addTarget(binding.coordinator)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
            interpolator = FastOutSlowInInterpolator()
        }
```

#### MaterialContainerTransform

MDC-Android 库中的容器转换称为 `MaterialContainerTransform`。默认情况下，这个 `Transition` 子类会作为共享元素过渡运行，当带有 `transitionName` 标记时，Android 过渡系统可以选择两个采用不同布局的视图。

首先在两个fragment中设置共享view的translationName

```xml
android:transitionName="transitionName"
```

  在fragment3中

```kotlin
// 设置退出和重进入动画
exitTransition = MaterialElevationScale(false).apply {
    duration = 300
}
reenterTransition = MaterialElevationScale(true).apply {
    duration = 300
}
val fragment4 = Fragment4()
val bundle = Bundle()
bundle.putString("param1", msg)
bundle.putString("param2", view.transitionName)
fragment4.arguments = bundle
requireActivity().supportFragmentManager.beginTransaction()
    .addSharedElement(view, view.transitionName)
    .replace(R.id.constraintLayout4, fragment4).addToBackStack(null).commit()
```

设置延迟动画

```kotlin
postponeEnterTransition()
view.doOnPreDraw { startPostponedEnterTransition() }
```

在fragment4中

```kotlin
sharedElementEnterTransition = MaterialContainerTransform().apply {
    duration = 500
    scrimColor = Color.TRANSPARENT
    setAllContainerColors(
        requireContext().obtainStyledAttributes(intArrayOf(com.google.android.material.R.attr.colorSurface))
            .use {
                it.getColor(0, Color.MAGENTA)
            })
}
```

为确保将 `MaterialElevationScale` 过渡应用到整个主屏幕在根布局下标记过渡组

```xml
android:transitionGroup="true"
```

效果图:

<img src="D:\OneDrive\桌面\动画可见\gif\Shared_transition_final.gif" alt="Shared_transition_final" style="zoom:25%;" />

#### MaterialElevationScale

在fragment5中

```kotlin
enterTransition = MaterialContainerTransform().apply {
    startView = requireActivity().findViewById(R.id.floatingActionBar1)
    endView = requireView().findViewById(R.id.materialCardView1)
    duration = 300
    scrimColor = Color.TRANSPARENT
    containerColor =
        requireContext().themeColor(com.google.android.material.R.attr.colorSurface)
    startContainerColor =
        requireContext().themeColor(com.google.android.material.R.attr.colorSecondary)
    endContainerColor =
        requireContext().themeColor(com.google.android.material.R.attr.colorSurface)
}
returnTransition = Slide().apply {
    duration = 300
    addTarget(R.id.materialCardView1)
}
```

在fragment3中

```kotlin
exitTransition = MaterialElevationScale(false).apply {
    duration = 300
}
reenterTransition = MaterialElevationScale(true).apply {
    duration = 300
}
val fragment5 = Fragment5()
requireActivity().supportFragmentManager.beginTransaction()
    .replace(R.id.constraintLayout4, fragment5).addToBackStack(null).commit()
```

<img src="D:\OneDrive\桌面\动画可见\gif\MaterialElevationScale.gif" alt="MaterialElevationScale" style="zoom:25%;" />

#### MaterialSharedAxis

  在fragment3

```kotlin
// 离开时放大过渡
exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
    duration = 300
}
// 重新返回时缩小过渡
reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
    duration = 300
}
val fragment6 = Fragment6()
requireActivity().supportFragmentManager.beginTransaction()
    .replace(R.id.constraintLayout4, fragment6).addToBackStack(null).commit()
```

在fragment6

```kotlin
// 进入时放大过渡
enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
    duration = 300
}
// 返回时缩小过渡
returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
    duration = 300
}
```

| MaterialSharedAxis.X                                         | MaterialSharedAxis.Y                                         | MaterialSharedAxis.Z                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="D:\OneDrive\桌面\动画可见\gif\MaterialSharedAxis-X.gif" alt="MaterialSharedAxis-X" style="zoom:25%;" /> | <img src="D:\OneDrive\桌面\动画可见\gif\MaterialSharedAxis-Y.gif" alt="MaterialSharedAxis-Y" style="zoom:25%;" /> | <img src="D:\OneDrive\桌面\动画可见\gif\MaterialSharedAxis-Z.gif" alt="MaterialSharedAxis-Z" style="zoom:25%;" /> |



#### MaterialFadeThrough

在fragment3

```kotlin
exitTransition = MaterialFadeThrough().apply {
                duration = 300
            }
            val fragment7 = Fragment7()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.constraintLayout4, fragment7).addToBackStack(null).commit()
```

在fragment7

```kotlin
enterTransition = MaterialFadeThrough().apply {
    duration = 300
}
```

<img src="D:\OneDrive\桌面\动画可见\gif\MaterialFadeThrough.gif" alt="MaterialFadeThrough" style="zoom:25%;" />

#### View的MaterialContainerTransform

```kotlin
// 展开动画
val transform = MaterialContainerTransform().apply {
            startView = chip
            endView = materialCardView2
            scrimColor = Color.TRANSPARENT
            endElevation = 3f
            addTarget(materialCardView2)
        }
        TransitionManager.beginDelayedTransition(constraintLayout, transform)
// 收缩动画
val transform = MaterialContainerTransform().apply {
            startView = materialCardView2
            endView = chip
            scrimColor = Color.TRANSPARENT
            startElevation = 3f
            addTarget(chip)
        }
        TransitionManager.beginDelayedTransition(constraintLayout, transform)
```

<img src="D:\OneDrive\桌面\动画可见\gif\View-MaterialContainerTransform.gif" alt="View-MaterialContainerTransform" style="zoom:25%;" />



### 其他的一些动画

#### RecyclerView入场动画

在xml里设置

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="200">
    <translate
        android:fromXDelta="-50%p"
        android:toXDelta="0" />
    <alpha
        android:fromAlpha="0.0"
        android:toAlpha="1.0" />
</set>
```

在初始化recyclerView时

```kotlin
recyclerView.layoutAnimation = // 入场动画
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.recycler_view_fade_in
                )
            )
```

效果图:

<img src="https://s2.loli.net/2022/07/29/ehwoWQDFGOZb8kz.gif" alt="RecyclerView-入场动画" style="zoom:25%;" />

#### ViewPager2的切换动画

先继承viewPager2.PageTransformer

```kotlin
private const val MAX_ROTATION = 90f
private const val MIN_SCALE = 0.9f
class SquareBoxTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.apply {
            pivotY = height / 2f
            when {
                position < -1 -> {
                    // This page is way off-screen to the left.
                    rotationY = -MAX_ROTATION
                    pivotX = width.toFloat()
                }
                position <= 1 -> {
                    rotationY = position * MAX_ROTATION
                    if (position < 0) {
                        pivotX = width.toFloat()
                        val scale =
                            MIN_SCALE + 4f * (1f - MIN_SCALE) * (position + 0.5f) * (position + 0.5f)
                        scaleX = scale
                        scaleY = scale
                    } else {
                        pivotX = 0f
                        val scale =
                            MIN_SCALE + 4f * (1f - MIN_SCALE) * (position - 0.5f) * (position - 0.5f)
                        scaleX = scale
                        scaleY = scale
                    }
                }
                else -> {
                    // This page is way off-screen to the right.
                    rotationY = MAX_ROTATION
                    pivotX = 0f
                }
            }
        }
    }
}
```

<img src="https://s2.loli.net/2022/07/29/BpNo46EwZGQaCqr.webp" alt="img" style="zoom: 67%;" />


|                | 前一个view的position变化 | 当前view的position变化 | 后一个view的position变化 |
| -------------- | ------------------------ | ---------------------- | ------------------------ |
| 当前view右滑时 | -1 ----> 0               | 0-------->1            | 1 ----> +∞               |
| 当前view左滑时 | -∞ ----> -1              | 0 -----> -1            | 1 ------->0              |

  参数page是ViewPager持有的页面(包括cureent page)的rootView，position是page相对于基准参考点的偏移量，滑动过程中可标识page的偏移程度，周期为1。根据基准参考点及page页面宽度归一化的描述，在SCROLL_STATE_IDLE状态下，current page的position为0，上一页的position为-1.0，下一页的position为1.0，依此类推。

然后在viewPager2中设置

```kotlin
mViewPager2.setPageTransformer(SquareBoxTransformer())
```

效果图:

<img src="D:\OneDrive\桌面\动画可见\gif\ViewPager2-切换动画.gif" alt="ViewPager2-切换动画" style="zoom:33%;" />

#### 自动为布局更新添加动画

在 Activity 的布局 XML 文件中，针对您要启用动画的布局，将 `android:animateLayoutChanges` 属性设置为 `true`。

```xml
 <LinearLayout android:id="@+id/container"
        android:animateLayoutChanges="true"
        ...
    />
```

在activity中

```kotlin
 lateinit var containerView: ViewGroup
    ...
    private fun addItem() {
        val newView: View = ...

        containerView.addView(newView, 0)
    }
```

效果图:

<img src="D:\OneDrive\桌面\动画可见\gif\布局更新动画.gif" alt="布局更新动画" style="zoom: 33%;" />





## Compose动画[补充]

### 为简单的值变化添加动画效果

> 切换标签页，内容的背景颜色会发生变化。
>
> ```kotlin
> val backgroundColor = if (tabPage == TabPage.Home) Purple100 else Green300
> ```
>
> 如果需要为简单值变化添加动画效果，可以使用 `animate*AsState` .这里使用`animateColorAsState`可组合向，创建动画值。
>
> 注意:返回的值是 `State<T>` 对象，可以使用包含 `by` 声明的本地委托属性，将该值视为普通变量。`Compose`中都会把State<T>对象使用by委托属性
>
> ```kotlin
> val backgroundColor by animateColorAsState(if (tabPage == TabPage.Home) Purple100 else Green300)
> ```
>
> 效果图:颜色变化
>
> <img src="https://s2.loli.net/2022/07/27/XqHFONVlIwhcURk.gif" alt="Compose-animateColorAsState" width="250dp" />

### 为可见性添加动画效果

> 使用 `if` 语句显示或隐藏"Edit"的文本。
>
> ```kotlin
> if (extended) {
>  Text(
>      text = stringResource(R.string.edit),
>      modifier = Modifier
>          .padding(start = 8.dp, top = 3.dp)
>  )
> }
> ```
>
> 为可见性变化添加动画，将 `if` 替换为 `AnimatedVisibility` 可组合项。
>
> ```kotlin
> AnimatedVisibility(extended) {
>  Text(
>      text = stringResource(R.string.edit),
>      modifier = Modifier
>          .padding(start = 8.dp, top = 3.dp)
>  )
> }
> ```
>
> 效果图:FAB的展开
>
> <img src="https://s2.loli.net/2022/07/27/WzescPJXxM7lGrI.gif" alt="Compose-AnimatedVisibility" style="zoom:33%;" />
>
> 默认情况下，`AnimatedVisibility` 会以淡入和展开的方式显示元素，以淡出和缩小的方式隐藏元素。

> 接下来自定义可见性动画，从而让元素从顶部滑入，然后滑出到顶部。
>
> 将`enter` 和 `exit` 参数添加到 `AnimatedVisibility` 可组合项中。
>
> `enter` 参数是 `EnterTransition` 的实例。使用 `slideInVertically` 函数创建 `EnterTransition`。`initialOffsetY` 参数是返回初始位置的 lambda。lambda 会收到一个表示元素高度的参数，只需返回其负值即可。使用 `slideInVertically` 时，滑入后的目标偏移量始终为 `0`（像素）。可使用 lambda 函数将 `initialOffsetY` 指定为绝对值或元素全高度的百分比。
>
> `animationSpec` 是指定动画值应如何随时间变化。这里，用 `tween` 函数创建, 时长为 150 毫秒，加/减速选项为 `LinearOutSlowInEasing`。
>
> 同理，可以对 `exit` 参数使用 `slideOutVertically` 函数。`slideOutVertically` 假定初始偏移量为 0，只需指定 `targetOffsetY`。使 `tween` 函数，但时长为 250 毫秒，加/减速选项为 `FastOutLinearInEasing`。
>
> 生成的代码应如下所示。
>
> ```Kotlin
> AnimatedVisibility(
>  visible = shown,
>  enter = slideInVertically(
>      // Enters by sliding down from offset -fullHeight to 0.
>      initialOffsetY = { fullHeight -> -fullHeight },
>      animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
>  ),
>  exit = slideOutVertically(
>      // Exits by sliding up from offset 0 to -fullHeight.
>      targetOffsetY = { fullHeight -> -fullHeight },
>      animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
>  )
> ) {
>  Surface(
>      modifier = Modifier.fillMaxWidth(),
>      color = MaterialTheme.colors.secondary,
>      elevation = 4.dp
>  ) {
>      Text(
>          text = stringResource(R.string.edit_message),
>          modifier = Modifier.padding(16.dp)
>      )
>  }
> }
> ```
>
> 效果图:消息现在是从顶部滑入和滑出。
>
> 
>
> <img src="https://s2.loli.net/2022/07/27/uoXHKFhv7nsBc9V.gif" alt="Compose-AnimatedVisibility-Custom" style="zoom:33%;" />

### 为内容大小添加动画效果

> 当正文显示或隐藏时，包含文本的卡片会展开或缩小。
>
> ```kotlin
> Column(
>  modifier = Modifier
>      .fillMaxWidth()
>      .padding(16.dp)
> ) {
>  // ... the title and the body
> }
> ```
>
> 此处的这个 `Column` 可组合项会在内容发生变化时更改其大小。添加 `animateContentSize` 修饰符，为其大小变化添加动画效果。
>
> ```kotlin
> Column(
>  modifier = Modifier
>      .fillMaxWidth()
>      .padding(16.dp)
>      .animateContentSize()
> ) {
>  // ... the title and the body
> }
> ```
>
> 效果图:
>
> <img src="https://s2.loli.net/2022/07/27/mvVMbNucZqQY1Wd.gif" alt="Compose-animateContentSize" style="zoom:33%;" />

### 多值动画

> 我们自定义了标签页指示器。它是当前所选标签页上显示的一个矩形。
>
> 如需同时为多个值添加动画效果，可使用 `Transition`。`Transition` 可使用 `updateTransition` 函数创建。将当前所选标签页的索引作为 `targetState` 参数传递。
>
> 每个动画值都可以使用 `Transition` 的 `animate*` 扩展函数进行声明。这里使用 `animateDp` 和 `animateColor`。它们会接受一个 lambda 块，可以为每个状态指定目标值。
>
> ```kotlin
> val transition = updateTransition(tabPage)
> val indicatorLeft by transition.animateDp { page ->
>  tabPositions[page.ordinal].left
> }
> val indicatorRight by transition.animateDp { page ->
>  tabPositions[page.ordinal].right
> }
> val color by transition.animateColor { page ->
>  if (page == TabPage.Home) Purple700 else Green800
> }
> ```
>
> 效果图: 点击标签页会更改 `tabPage` 状态的值，这时与 `transition` 关联的所有动画值会开始以动画方式切换至为目标状态指定的值。
>
> <img src="https://s2.loli.net/2022/07/27/xt2P8iQW5mXjfwv.gif" alt="Compose-Transition-多值动画1" style="zoom:33%;" />
>
> 此外，可以指定 `transitionSpec` 参数来自定义动画行为。例如可以让靠近目标页面的一边比另一边移动得更快来实现指示器的弹性效果。可以在 `transitionSpec`中使用 `isTransitioningTo` infix 函数来确定状态变化的方向。
>
> ```kotlin
> val transition = updateTransition(
>  tabPage,
>  label = "Tab indicator"
> )
> val indicatorLeft by transition.animateDp(
>  transitionSpec = {
>      if (TabPage.Home isTransitioningTo TabPage.Work) {
>          // Indicator moves to the right.
>          // The left edge moves slower than the right edge.
>          spring(stiffness = Spring.StiffnessVeryLow)
>      } else {
>          // Indicator moves to the left.
>          // The left edge moves faster than the right edge.
>          spring(stiffness = Spring.StiffnessMedium)
>      }
>  },
>  label = "Indicator left"
> ) { page ->
>  tabPositions[page.ordinal].left
> }
> val indicatorRight by transition.animateDp(
>  transitionSpec = {
>      if (TabPage.Home isTransitioningTo TabPage.Work) {
>          // Indicator moves to the right
>          // The right edge moves faster than the left edge.
>          spring(stiffness = Spring.StiffnessMedium)
>      } else {
>          // Indicator moves to the left.
>          // The right edge moves slower than the left edge.
>          spring(stiffness = Spring.StiffnessVeryLow)
>      }
>  },
>  label = "Indicator right"
> ) { page ->
>  tabPositions[page.ordinal].right
> }
> val color by transition.animateColor(
>  label = "Border color"
> ) { page ->
>  if (page == TabPage.Home) Purple700 else Green800
> }
> ```
>
> 效果图:
>
> <img src="https://s2.loli.net/2022/07/27/TGzJo72are9R63b.gif" alt="Compose-Transition-多值动画2" style="zoom:33%;" />
>
> Android Studio 支持在 Compose 预览中检查过渡效果。如需使用**动画预览**，请在预览中点击可组合项右上角的“Start interactive mode”图标，以开始交互模式。
>
> 

### 重复动画

> ```kotlin
> val alpha = 1f
> ```
>
>  `InfiniteTransition`可以实现重复动画,先使用 `rememberInfiniteTransition` 函数。然后，可以使用 `InfiniteTransition` 的一个 `animate*` 扩展函数声明每个动画值变化。这里为 Alpha 值添加动画效果，`initialValue` 参数应为 `0f`，而 `targetValue` 应为 `1f`。然后为动画指定 `InfiniteRepeatableSpec`,使其重复。
>
> ```kotlin
>val infiniteTransition = rememberInfiniteTransition()
> val alpha by infiniteTransition.animateFloat(
> initialValue = 0f,
> targetValue = 1f,
>  animationSpec = infiniteRepeatable(
>    animation = keyframes {
>        durationMillis = 1000
>          0.7f at 500
>      },
>      repeatMode = RepeatMode.Reverse
>    )
>    )
>  ```
> 
> 效果图:
>
> <img src="https://s2.loli.net/2022/07/27/aCVpfHsGh9w4QOZ.gif" alt="Compose-InfiniteTransition" style="zoom:33%;" />

### 手势动画

> 将动画值与来自触摸事件的值同步。
>
> 创建一个修饰符，以使触摸时元素可滑动。当元素被快速滑动到屏幕边缘时，调用 `onDismissed` 回调，移除该元素。
>
> `Animatable`可表示滑动元素的水平偏移量。
>
> ```kotlin
> val offsetX = remember { Animatable(0f) } /
> pointerInput {
>  // Used to calculate a settling position of a fling animation.
>  val decay = splineBasedDecay<Float>(this)
>  // Wrap in a coroutine scope to use suspend functions for touch events and animation.
>  coroutineScope {
>      while (true) {
>          // ...
> ```
>
> 如果动画当前正在运行，应将其拦截。可以通过调用 Animatable的stop() 来实现
>
> ```kotlin
> // Wait for a touch down event.
> val pointerId = awaitPointerEventScope { awaitFirstDown().id }
> offsetX.stop() 
> // Prepare for drag events and record velocity of a fling.
> val velocityTracker = VelocityTracker()
> // Wait for drag events.
> awaitPointerEventScope {
> ```
>
> 不断接收到拖动事件。必须将触摸事件的位置同步到动画值中。使用Animatable的snapTo。
>
> ```kotlin
> horizontalDrag(pointerId) { change ->
>  // Add these 4 lines
>  val horizontalDragOffset = offsetX.value + change.positionChange().x
>  launch {
>      offsetX.snapTo(horizontalDragOffset)
>  }
>  // Record the velocity of the drag.
>  velocityTracker.addPosition(change.uptimeMillis, change.position)
>  // Consume the gesture event, not passed to external
>  change.consumePositionChange()
> }
> ```
>
> 计算快速滑动操作的最终位置，确定是要将元素滑回原始位置，还是滑开元素
>
> ```kotlin
> // Dragging finished. Calculate the velocity of the fling.
> val velocity = velocityTracker.calculateVelocity().x
> // Add this line
> val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)
> ```
>
> 为 `Animatable` 设置值的上下界限，使到达界限时立即停止。使用 `pointerInput` 修饰符，可以通过 `size` 属性访问元素的大小,获取界限。
>
> ```kotlin
> offsetX.updateBounds(
>  lowerBound = -size.width.toFloat(),
>  upperBound = size.width.toFloat()
> )
> ```
>
> 首先来比较之前计算的快速滑动操作的最终位置以及元素的大小。如果最终位置低于该大小，则表示快速滑动的速度不够。使用 `animateTo` 将值的动画效果设置回 0f。否则，可以使用 `animateDecay` 来开始播放快速滑动动画。
>
> ```kotlin
> launch {
>  if (targetOffsetX.absoluteValue <= size.width) {
>      // Not enough velocity; Slide back.
>      offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
>  } else {
>      // Enough velocity to slide away the element to the edge.
>      offsetX.animateDecay(velocity, decay)
>      // The element was swiped away.
>      onDismissed()
>  }
> }
> ```
>
> 最后对元素应用偏移。
>
> ```kotlin
> .offset { IntOffset(offsetX.value.roundToInt(), 0) }
> ```
>
> 最终代码：
>
> ```kotlin
> private fun Modifier.swipeToDismiss(
>  onDismissed: () -> Unit
> ): Modifier = composed {
>  // This `Animatable` stores the horizontal offset for the element.
>  val offsetX = remember { Animatable(0f) }
>  pointerInput(Unit) {
>      // Used to calculate a settling position of a fling animation.
>      val decay = splineBasedDecay<Float>(this)
>      // Wrap in a coroutine scope to use suspend functions for touch events and animation.
>      coroutineScope {
>          while (true) {
>              // Wait for a touch down event.
>              val pointerId = awaitPointerEventScope { awaitFirstDown().id }
>              // Interrupt any ongoing animation.
>              offsetX.stop()
>              // Prepare for drag events and record velocity of a fling.
>              val velocityTracker = VelocityTracker()
>              // Wait for drag events.
>              awaitPointerEventScope {
>                  horizontalDrag(pointerId) { change ->
>                      // Record the position after offset
>                      val horizontalDragOffset = offsetX.value + change.positionChange().x
>                      launch {
>                          // Overwrite the `Animatable` value while the element is dragged.
>                          offsetX.snapTo(horizontalDragOffset)
>                      }
>                      // Record the velocity of the drag.
>                      velocityTracker.addPosition(change.uptimeMillis, change.position)
>                      // Consume the gesture event, not passed to external
>                      change.consumePositionChange()
>                  }
>              }
>              // Dragging finished. Calculate the velocity of the fling.
>              val velocity = velocityTracker.calculateVelocity().x
>              // Calculate where the element eventually settles after the fling animation.
>              val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)
>              // The animation should end as soon as it reaches these bounds.
>              offsetX.updateBounds(
>                  lowerBound = -size.width.toFloat(),
>                  upperBound = size.width.toFloat()
>              )
>              launch {
>                  if (targetOffsetX.absoluteValue <= size.width) {
>                      // Not enough velocity; Slide back to the default position.
>                      offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
>                  } else {
>                      // Enough velocity to slide away the element to the edge.
>                      offsetX.animateDecay(velocity, decay)
>                      // The element was swiped away.
>                      onDismissed()
>                  }
>              }
>          }
>      }
>  }
>      // Apply the horizontal offset to the element.
>      .offset { IntOffset(offsetX.value.roundToInt(), 0) }
> }
> ```
>
> 效果图:滑动删除
>
> <img src="https://s2.loli.net/2022/07/27/ADLPQNUysHxIpz1.png" alt="Compose-Animatable-手势动画" style="zoom:25%;" />

### 小结

> - 使用`animateContentSize` 和 `AnimatedVisibility` 构建几种常见的动画模式。
> - 使用 `animate*AsState` 为单个值添加动画效果
> - 使用 `updateTransition` 为多个值添加动画效果
> - 使用 `infiniteTransition` 为多个值无限期地添加动画效果 
> - 使用 `Animatable` 构建了与触摸手势相结合的自定义动画。



参考文章:

- [Jetpack Compose 动画 ](https://developer.android.google.cn/codelabs/jetpack-compose-animation?continue=https%3A%2F%2Fdeveloper.android.google.cn%2Fcourses%2Fpathways%2Fcompose%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fjetpack-compose-animation#8)
- [Anroid自定义控件开发入门与实战-启舰]

本文源码已上传github: [WhiteNight123/AnimationDemo (github.com)](https://github.com/WhiteNight123/AnimationDemo)
