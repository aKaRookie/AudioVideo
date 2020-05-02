package  com.fhl.test

import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.*

/**
 *
 * @ProjectName:    MoGoModuleOnlineCar
 * @Package:        com.mogo.module.v2x.utils
 * @ClassName:      LocationUtils
 * @Description:    java类作用描述
 * @Author:         fenghl
 * @CreateDate:     2020/4/9 18:24
 * @UpdateUser:     更新者：
 * @UpdateDate:     2020/4/9 18:24
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */
class LocationUtils {
    companion object {
        /**
         * 地球赤道半径(km)
         * */
        private val EARTH_RADIUS = 6378.137

        /**
         * 转化为弧度(rad)
         * */
        fun rad(d: Double) = d * PI / 180.0
        /**********************************************
         *                 北 0°
         *                  |     目标车
         *                  |    /
         *                  |~~/方位角
         *                  |/
         *西 270°          源车           东 90°
         *
         *
         *                南 180°
         ********************************************/
        /**
         * 计算方位和距离
         * @param lat1 源 纬度
         * @param lng1 源 经度
         * @param dstLat 目标 纬度
         * @param dstLng 目标经度
         */
        fun getAngleAndDistance(
            lat1: Double,
            lng1: Double,
            dstLat: Double,
            dstLng: Double
        ): Array<String?> {


            var obj = arrayOfNulls<String>(2)
            for (s in obj) {

            }

            val a = toRadians(90 - dstLat)

            val b = toRadians(90 - lat1)

            val ab = toRadians(dstLng - lng1)

            var cosc = cos(a) * cos(b) + sin(a) * sin(b) * cos(ab)

            if (cosc < -1.0) cosc = -1.0

            if (cosc > 1.0) cosc = 1.0

            val c = acos(cosc)

            var sinA = (sin(a) * sin(ab)) / sin(c)

            if (sinA < -1.0) sinA = -1.0

            if (sinA > 1.0) sinA = 1.0

            val A = asin(sinA)

            var angle = toDegrees(A)
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

                angle = 360 + angle

            } else {//第三，四象限

                angle = 180 - angle

            }
            println("angle= $angle")
            if (obj[0] == null) {

                if (angle <= 22.5 || angle > 337.5) {

                    obj[0] = "正北"

                } else if (angle > 22.5 && angle <= 67.5) {

                    obj[0] = "东北"

                } else if (angle > 67.5 && angle <= 112.5) {

                    obj[0] = "正东"

                } else if (angle > 112.5 && angle <= 157.5) {

                    obj[0] = "东南"

                } else if (angle > 157.5 && angle <= 202.5) {

                    obj[0] = "正南"

                } else if (angle > 202.5 && angle <= 247.5) {

                    obj[0] = "西南"

                } else if (angle > 247.5 && angle <= 247.5) {

                    obj[0] = "正西"

                } else if (angle > 247.5 && angle <= 337.5) {

                    obj[0] = "西北"

                }

            }

            obj[1] = getDistance(lat1, lng1, dstLat, dstLng)

            return obj

        }

        /**
         * 根据两点计算距离
         */
        fun getDistance(lat1: Double, lng1: Double, dstLat: Double, dstLng: Double): String {
            val radLat1 = lat1 * PI / 180.0

            val raddstLat = dstLat * PI / 180.0

            val a = radLat1 - raddstLat

            val b = lng1 * PI / 180.0 - dstLng * PI / 180.0

            var s = 2 * asin(
                sqrt(
                    sin(a / 2).pow(2.0) +

                            cos(radLat1) * cos(raddstLat) * sin(b / 2).pow(2.0)
                )
            )

            s *= EARTH_RADIUS

            s = (s * 10000).roundToLong() * 1.0 / 10000
            return s.toString()
        }

        /**
         * 计算方位角
         * @param dstLat 目标维度
         * @param dstLng 目标精度
         * @param srcLat 源 维度
         * @param srcLng 源 精度
         */
        fun computeBearing(
            dstLat: Double,
            dstLng: Double,
            srcLat: Double,
            srcLng: Double
        ): Double {

            var latA = dstLat
            var lngA = dstLng
            var latB = srcLat
            var lngB = srcLng

            latA = latA * PI / 180
            lngA = lngA * PI / 180
            latB = latB * PI / 180
            lngB = lngB * PI / 180

            var d =
                sin(latA) * sin(latB) + cos(latA) * cos(latB) * cos(lngB - lngA)
            d = sqrt(1 - d * d)
            d = cos(lngB) * sin(lngB - lngA) / d
            d = asin(d) * 180 / PI
            //d = round(d*10000)
            return d
        }
    }

}