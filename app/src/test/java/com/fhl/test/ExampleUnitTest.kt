package com.fhl.test


import  com.fhl.test.LocationUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates
import java.lang.Math.pow
import kotlin.math.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        val x = 10
        var a: Int? = 0
        if (x > 0) {
            println("$x sss")
        }
    }

    @Test
    fun testNull() {
        var name: String? = "fitkit"
        val len = if (name != null) name.length else -1
        println("name的长度: $len")
        println(name?.length?.toString())
        name = null
        if (name != null && name.length > 0) {
            println("name: $name")
        } else {
            println("空字符串！")
        }
    }

    @Test
    fun testLet() {
        val myArr: Array<String?> = arrayOf("fkit", "fkjava", null, "crazyit")
        myArr.forEach { item -> item?.let { println("array: $item") } }

        for (item in myArr) {
            item?.let { println(item) }
        }
    }

    @Test
    fun testElvis() {
        var name: String? = "fkit"
        val b = name?.length ?: -1
        println("boolean: $b")
    }

    @Test
    fun testNPE() {
        val name: String? = "fkit"
        println(name!!.length)

        for (c in name.toCharArray()) {
            c.let {
                println("char: $c")
                println("char: $c")
            }
        }
    }

    @Test
    fun testArray() {
        val array = "fkjava.org"
        println(array[2])
        for (c in array) {
            print(c)
        }
        println()

        var str = "fkjava.org"
        println(str.length)

        val txt = """
            |天上白玉京
            |十二楼五城
            |现任福我顶
            |结发受长生""".trimMargin()
        println(txt)
    }

    @Test
    fun testTime() {
        var t1 = 1573974000000
        var t2 = System.currentTimeMillis()
        println("time long: t1=$t1,t2=$t2")
        var time = SimpleDateFormat("HH:mm:ss").format(Date(t2))
        println("time: $time")
    }

    class A(var x: Int, private val y: Int) {
        val isEqual: Boolean
            get() = x == y
    }

    @Test
    fun testCmt1() {
        val a = A(1, 1)
        println("is equal: ${a.isEqual}")

    }

    @Test
    fun calculateTime() {

        var duration: Long = 1574419517_000
        var time = ""
        val hours = 0
        val minute = (duration / 60000).toLong()
        val seconds = (duration % 60000).toLong()
        val second = Math.floor(seconds.toDouble() / 1000).toLong()
        println("时间: hours:" + hours)

        if (minute < 10) {
            time += "0"
        }
        time += "$minute:"
        if (second < 10) {
            time += "0"
        }
        time += second
        val format = SimpleDateFormat("HH:mm:ss")
        val t1 = System.currentTimeMillis()
        time = format.format(duration)
        println("时间: time:$time, 耗时:${System.currentTimeMillis() - t1}")
    }

    @Test
    fun testMath() {
        val num = math("add", 3, 2)
        println("num: $num")
    }


    private fun math(type: String, n1: Int, n2: Int): Int {
        fun add(n1: Int, n2: Int): Int = n1 + n2
        fun sub(n1: Int, n2: Int): Int = n1 - n2
        val myfun: (Int, Int) -> Int
        myfun = when (type) {
            "add" -> {
                ::add
            }
            else -> {
                ::sub
            }
        }

        fun out(type: String, func: (Int, Int) -> Int) {
            val n: Int = func(1, 2)
            println("type:$type, out: $n")
        }
        out(type, myfun)


        return myfun(n1, n2)
    }

    private val lambdaList: ArrayList<(Int) -> Int> = ArrayList()
    private fun collectFn(fn: (Int) -> Int) {
        lambdaList.add(fn)
    }

    @Test
    fun testLambda1() {
        collectFn { it * it }
        collectFn { it * it * it }
        println("size: ${lambdaList.size}")
        for (i in lambdaList.indices) {
            println("for: ${lambdaList[i](i + 10)}")
        }
    }

    inline fun map(data: Array<Int>, fn: (Int) -> Int): Array<Int> {
        val result = Array<Int>(data.size) { 0 }
        for (i in data.indices) {
            result[i] = fn(data[i])
        }
        return result
    }

    @Test
    fun testInline() {
        val arr = arrayOf(20, 4, 40, 100, 39)
        val mapResult = map(arr) { it + 3 }
        println(mapResult.contentToString())
    }

    inline fun noInline(fn1: (Int) -> Int, noinline fn2: (String) -> String) {
        println(fn1(20))
        println(fn2("hahah"))
    }

    @Test
    fun testNoInline() {
        noInline({ it * it }, { "say $it" })
    }

    inline fun f(crossinline body: () -> Unit) {
        val f = Runnable { body() }
        Thread(f).start()

    }

    @Test
    fun testCrossLine() {
        f { println("cross inline") }
    }

    class ApplePack(weight: Double) {
        var weight: Double = weight
        override fun toString(): String {
            return "ApplePack(weight=$weight)"
        }
    }

    class Apple(weight: Double) {
        var weight: Double = weight
        override fun toString(): String {
            return "Apple(weight=$weight)"
        }

        infix fun add(other: Apple): ApplePack {
            return ApplePack(weight + other.weight)
        }

        infix fun sub(other: Apple): Apple {
            weight -= other.weight
            return this
        }
    }

    @Test
    fun testInfix() {
        var origin = Apple(3.4)
        val ap = origin add (Apple(2.4))
        println(ap)
        origin sub Apple(1.4)
        println(origin)
    }


    class User(name: String, pass: String, age: Int) {
        var name = name
        var pass = pass
        var age = age
        operator fun component1(): String {
            return this.name
        }

        operator fun component2(): String {
            return this.pass
        }

        operator fun component3(): Int {
            return this.age
        }
    }

    @Test
    fun testComponent() {
        val user = User("lao wang", "123321", 23)
        val (name, pass: String) = user
        println(name)
        println(pass)
        //将User对象解构给3个变量
        //利用user对象的component1(),component2(),component3()方法
        user.name = "老罗"
        var (name2, pass2, age2) = user
        println(name2)
        println(pass2)
        println(age2)

        var (_, pass3, age3) = user

        println(pass3)
        println(age3)

    }

    private fun time2String(t: Long) = if (t < 10) {
        "0$t"
    } else {
        "$t"
    }

    private fun getTimeFormat(second: Long): String {
        val hh = (second / 3600)//把秒除得时间
        val mm = (second % 3600) / 60//把余数用于除得分钟
        val ss = (second % 3600) % 60//最后的余数直接就是秒钟
        val tss = time2String(ss)
        val tmm = time2String(mm)
        val thh = time2String(hh)
        return "$thh:$tmm:$tss"
    }

    @Test
    fun testTimeFormat() {
        val second = 60L
        println("${second}s转换为:${getTimeFormat(second)}")
    }

    interface Base {
        val msg: String
        fun printMsg()
    }

    class BaseImpl(val x: Int) : Base {
        override val msg: String
            get() = "hello"

        override fun printMsg() {
            println("message:$msg")
        }

    }

    class Devired(b: Base) : Base by b {
        override val msg: String
            get() = "Devired message"
    }

    @Test
    fun testDelegate() {
        val b = BaseImpl(10)
        val d = Devired(b)
        println(d.msg)
        d.printMsg()

    }

    class Example {
        var p: Devired by Delegates.notNull()
    }

    @Test
    fun testDelegate1() {
        val e = Example()
        val b = BaseImpl(10)
        val d = Devired(b)
        e.p = d
        println(e.p.msg)
        e.p.printMsg()


    }

    val lazyValue: String by lazy(mode = LazyThreadSafetyMode.NONE) {
        println("********")
        println("computed!")
        "Hello"
    }

    @Test
    fun testLazy() {
        println(lazyValue)
        println(lazyValue)
    }

    class Person {
        var name: String by Delegates.observable("duck") { property, old, new ->
            println("prop:$property, $old = $new")
        }
        var age: Int by Delegates.vetoable(0, { property, oldValue, newValue ->
            println("prop:$property, $oldValue:$newValue")
            oldValue < newValue


        })
    }

    @Test
    fun testDelegateObserver() {
        val p = Person()
        p.name = "tom"
        println(p.name)
        p.name = "jerry"
        println(p.name)

        p.age = 5
        println("age:${p.age}")
        p.age = 10
        println("age:${p.age}")
        p.age = 8
        println("age:${p.age}")
        p.age = 18
        println("age:${p.age}")
    }

    @Test
    fun testDirection() {
        //昌平
        val lat1 = 40.22
        val lng1 = 116.2
        //目标大兴
        val dstLat = 39.73
        val dstLng = 116.33
        println("输入经纬度1={$lat1,$lng1)  经纬度2={$dstLat,$dstLng) ")
        val array1 = getAngleAndDistance(lat1, lng1, lat1, lng1)
        for (s in array1) {
            println("目标 大兴在昌平的${s}处")
        }
        println("--------------------------------------------------")
        val angle = getAngleByGps(lat1, lng1, dstLat, dstLng)
        val distance1 = getDistanceByGps(lat1, lng1, dstLat, dstLng)
        val angle2 = LocationUtils.computeBearing(dstLat, dstLng, lat1, lng1)
        val angle3 = GetAzimuth(lat1, lng1, dstLat, dstLng)

        println("角度1:$angle")
        println("距离1:$distance1")


        println("角度2:$angle2")
        println("角度3:$angle3")

    }

    /**
     * 地球赤道半径(km)
     * */
    public val EARTH_RADIUS = 6378.137;
    /**
     * 地球每度的弧长(km)
     * */
    val EARTH_ARC = 111.199;

    /**
     * 转化为弧度(rad)
     * */
    fun rad(d: Double) = d * Math.PI / 180.0

    /**
     * 求两经纬度方向角
     *
     * @param lon1
     *            第一点的经度
     * @param lat1
     *            第一点的纬度
     * @param lon2
     *            第二点的经度
     * @param dstLat
     *            第二点的纬度
     * @return 方位角，角度（单位：°）
     * */
    fun GetAzimuth(
        lat11: Double, lon11: Double, dstLat2: Double, lon22: Double

    ): Double {
        var lat1 = rad(lat11);
        var dstLat = rad(dstLat2);
        var lon1 = rad(lon11);
        var lon2 = rad(lon22);
        var azimuth =
            Math.sin(lat1) * Math.sin(dstLat) + Math.cos(lat1) * Math.cos(dstLat) * Math.cos(lon2 - lon1);
        azimuth = Math.sqrt(1 - azimuth * azimuth);
        azimuth = Math.cos(dstLat) * Math.sin(lon2 - lon1) / azimuth
        azimuth = Math.asin(azimuth) * 180 / Math.PI;
        if (azimuth.isNaN()) {
            if (lon1 < lon2) {
                azimuth = 90.0;
            } else {
                azimuth = 270.0;
            }
        }


        return azimuth;
    }

    fun getAngleByGps(lat1: Double, lng1: Double, dstLat: Double, dstLng: Double): Double {
        val PI = Math.PI
        val x = sin(dstLng - lng1) * cos(dstLat)
        val y = cos(lat1) * sin(dstLat) - sin(lat1) * cos(dstLat) * cos(dstLng - lng1)
        val angle = atan2(x, y) * 180 / PI
        return if (angle > 0) angle else angle + 360
    }

    fun getDistanceByGps(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val PI = 3.1415926
        val EarthRadius = 6378137.0
        val Rad = PI / 180.0

        val radlat1 = lat1 * Rad
        val radlat2 = lat2 * Rad
        val a = radlat1 - radlat2
        val b = (lng1 - lng2) * Rad
        var s =
            2 * asin(sqrt(pow(sin(a / 2), 2.0) + cos(radlat1) * cos(radlat2) * pow(sin(b / 2), 2.0)))
        s = s * EarthRadius
        s = round(s * 10000) / 10000
        return s
    }

    private fun gps2d(lat_a: Double, lng_a: Double, lat_b: Double, lng_b: Double): Double {

        var lat_a = lat_a
        var lng_a = lng_a
        var lat_b = lat_b
        var lng_b = lng_b
        var d = 0.0
        lat_a = lat_a * Math.PI / 180
        lng_a = lng_a * Math.PI / 180
        lat_b = lat_b * Math.PI / 180
        lng_b = lng_b * Math.PI / 180

        d =
            Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a)
        d = Math.sqrt(1 - d * d)
        d = Math.cos(lat_b) * Math.sin(lng_b - lng_a) / d
        d = Math.asin(d) * 180 / Math.PI
        //d = Math.round(d*10000);
        return d
    }

    fun getAngleAndDistance(
        lat1: Double,
        lng1: Double,
        dstLat: Double,
        dstLng: Double
    ): Array<String?> {


        var obj = arrayOfNulls<String>(2)
        for (s in obj) {

        }

        val a = Math.toRadians(90 - dstLat)

        val b = Math.toRadians(90 - lat1)

        val ab = Math.toRadians(dstLng - lng1)

        var cosc = Math.cos(a) * Math.cos(b) + Math.sin(a) * Math.sin(b) * Math.cos(ab)

        if (cosc < -1.0) cosc = -1.0

        if (cosc > 1.0) cosc = 1.0

        val c = Math.acos(cosc)

        var sinA = (Math.sin(a) * Math.sin(ab)) / Math.sin(c)

        if (sinA < -1.0) sinA = -1.0

        if (sinA > 1.0) sinA = 1.0

        val A = Math.asin(sinA)

        var Aangle = Math.toDegrees(A)
        //精度相同
        if (dstLng == lng1) {

            if (dstLat > lat1) {

                obj[0] = "正北"

            } else if (dstLat < lat1) {

                obj[0] = "正南"

            } else {

                obj[0] = "正中"

            }
            //维度相同
        } else if (dstLat == lat1) {

            if (dstLng > lng1) {

                obj[0] = "正东"

            } else if (dstLng < lng1) {

                obj[0] = "正西"

            } else {

                obj[0] = "正中"

            }

        } else if (dstLng > lng1 && dstLat > lat1) {//B相对于A来说位于第一象限

        } else if (dstLng < lng1 && dstLat > lat1) {//第二象限

            Aangle = 360 + Aangle

        } else {//第三，四象限

            Aangle = 180 - Aangle

        }
        println("目标 大兴在昌平angle= $Aangle")
        if (obj[0] == null) {

            if (Aangle <= 22.5 || Aangle > 337.5) {

                obj[0] = "正北"

            } else if (Aangle > 22.5 && Aangle <= 67.5) {

                obj[0] = "东北"

            } else if (Aangle > 67.5 && Aangle <= 112.5) {

                obj[0] = "正东"

            } else if (Aangle > 112.5 && Aangle <= 157.5) {

                obj[0] = "东南"

            } else if (Aangle > 157.5 && Aangle <= 202.5) {

                obj[0] = "正南"

            } else if (Aangle > 202.5 && Aangle <= 247.5) {

                obj[0] = "西南"

            } else if (Aangle > 247.5 && Aangle <= 247.5) {

                obj[0] = "正西"

            } else if (Aangle > 247.5 && Aangle <= 337.5) {

                obj[0] = "西北"

            }

        }

        obj[1] = getDistance(lat1, lng1, dstLat, dstLng)

        return obj

    }

    fun getDistance(lat1: Double, lng1: Double, dstLat: Double, dstLng: Double): String {
        val radLat1 = lat1 * Math.PI / 180.0

        val raddstLat = dstLat * Math.PI / 180.0

        val a = radLat1 - raddstLat

        val b = lng1 * Math.PI / 180.0 - dstLng * Math.PI / 180.0

        var s = 2 * Math.asin(
            Math.sqrt(
                Math.pow(Math.sin(a / 2), 2.0) +

                        Math.cos(radLat1) * Math.cos(raddstLat) * Math.pow(Math.sin(b / 2), 2.0)
            )
        )

        s = s * 6378.137

        s = (s * 10000).roundToLong() * 1.0 / 10000
        return s.toString()
    }


}
