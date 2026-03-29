package com.posticket.app

import android.content.Context
import dalvik.system.PathClassLoader

class IntegratedPrinterManager(private val context: Context) {

    companion object {
        const val INTEGRATED_PRINTER_ID = "INTERNAL:CS10"
        private const val DEMO_PACKAGE = "print.apidemo.activity"
        private const val API_CLASS = "vpos.apipackage.PosApiHelper"
    }

    fun isAvailable(): Boolean {
        return runCatching {
            val appInfo = context.packageManager.getApplicationInfo(DEMO_PACKAGE, 0)
            appInfo.sourceDir?.isNotBlank() == true
        }.getOrDefault(false)
    }

    fun getDevice(): PrinterDevice? {
        return if (isAvailable()) {
            PrinterDevice(
                name = "Imprimante integree CS10",
                address = INTEGRATED_PRINTER_ID
            )
        } else {
            null
        }
    }

    fun printText(lines: List<String>) {
        val bridge = createBridge()
        bridge.init()
        bridge.print(lines.joinToString(separator = "\n", postfix = "\n\n\n"))
        bridge.start()
    }

    fun checkStatus(): Int {
        return createBridge().checkStatus()
    }

    private fun createBridge(): PosApiBridge {
        val appInfo = context.packageManager.getApplicationInfo(DEMO_PACKAGE, 0)
        val packageContext = context.createPackageContext(
            DEMO_PACKAGE,
            Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
        )
        val classLoader = PathClassLoader(
            appInfo.sourceDir,
            appInfo.nativeLibraryDir,
            packageContext.classLoader
        )

        val helperClass = classLoader.loadClass(API_CLASS)
        val instance = helperClass.getMethod("getInstance").invoke(null)
        return PosApiBridge(helperClass, instance)
    }

    private class PosApiBridge(
        private val helperClass: Class<*>,
        private val helperInstance: Any
    ) {
        fun init() {
            invokeInt("PrintInit")
        }

        fun print(text: String) {
            invokeInt("PrintStr", text)
        }

        fun start() {
            invokeInt("PrintStart")
        }

        fun checkStatus(): Int {
            return invokeInt("PrintCheckStatus")
        }

        private fun invokeInt(name: String, vararg args: Any): Int {
            val types = args.map { arg ->
                when (arg) {
                    is Int -> Int::class.javaPrimitiveType
                    is Byte -> Byte::class.javaPrimitiveType
                    else -> arg.javaClass
                }
            }.toTypedArray()

            val method = helperClass.getMethod(name, *types)
            return method.invoke(helperInstance, *args) as Int
        }
    }
}
