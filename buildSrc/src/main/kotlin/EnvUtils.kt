object EnvUtils {
    fun isAct():Boolean { // ACT 환경인 경우 true
        return System.getenv("ACT") != null
    }
}
