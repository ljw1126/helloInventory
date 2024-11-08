object EnvUtils {
    fun isAct():Boolean { // ACT 환경인 경우 true
        return System.getenv("ACT") != null
    }

    fun isLocal(): Boolean {
        return System.getenv("CI") == null
    }

    fun isCI(): Boolean {
        return System.getenv("CI") != null
    }
}
