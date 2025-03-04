package cc.ioctl.hook.experimental;

import static io.github.qauxv.util.HostInfo.getHostInfo;
import static io.github.qauxv.util.HostInfo.requireMinQQVersion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cc.ioctl.util.HookUtils;
import de.robv.android.xposed.XposedBridge;
import io.github.qauxv.base.annotation.FunctionHookEntry;
import io.github.qauxv.base.annotation.UiItemAgentEntry;
import io.github.qauxv.dsl.FunctionEntryRouter;
import io.github.qauxv.hook.CommonSwitchFunctionHook;
import io.github.qauxv.util.QQVersion;
import io.github.qauxv.util.SyncUtils;
import java.io.File;
import java.lang.reflect.Method;

@FunctionHookEntry
@UiItemAgentEntry
public class FixEnvironment extends CommonSwitchFunctionHook {

    private FixEnvironment() {
        super(null, true, null, SyncUtils.PROC_MSF);
    }

    @NonNull
    @Override
    public String getName() {
        return "尝试修复环境";
    }

    @Nullable
    @Override
    public String getDescription() {
        return "";
    }

    @NonNull
    @Override
    public String[] getUiItemLocation() {
        return FunctionEntryRouter.Locations.Auxiliary.EXPERIMENTAL_CATEGORY;
    }

    @Override
    public boolean isAvailable() {
        return requireMinQQVersion(QQVersion.QQ_8_9_28);
    }

    public static final FixEnvironment INSTANCE = new FixEnvironment();

    @Override
    public boolean initOnce() throws Exception {
        nativeInitEnvironmentHook();
        return true;
    }

    private native boolean nativeInitEnvironmentHook();
}
