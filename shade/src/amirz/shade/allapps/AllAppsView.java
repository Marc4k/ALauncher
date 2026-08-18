package amirz.shade.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsContainerView;

import amirz.shade.search.AllAppsSearchBackground;

public class AllAppsView extends AllAppsContainerView {
    private final AllAppsSpring mController;

    public AllAppsView(Context context) {
        this(context, null);
    }

    public AllAppsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mController = new AllAppsSpring(this);
    }

    @Override
    public void setDampedScrollShift(float shift) {
        float maxShift = getSearchView().getHeight() * 0.5f;
        float oldShift = Utilities.boundToRange(shift, -maxShift, maxShift);

        if (shift < 0f) {
            maxShift *= -1f;
        }
        float fact = shift / maxShift;
        float newShift = fact / (fact + 1f) * maxShift;

        super.setDampedScrollShift(0.3f * oldShift + 0.7f * newShift);
    }

    @Override
    public RecyclerView.EdgeEffectFactory createEdgeEffectFactory() {
        return new RecyclerView.EdgeEffectFactory() {
            @NonNull
            @Override
            protected EdgeEffect createEdgeEffect(@NonNull RecyclerView view, int direction) {
                switch (direction) {
                    case DIRECTION_TOP: return mController.createSide(+1f);
                    case DIRECTION_BOTTOM: return mController.createSide(-1f);
                }
                return super.createEdgeEffect(view, direction);
            }
        };
    }

    @Override
    public void setupHeader() {
        super.setupHeader();
        getFloatingHeaderView().reset(false);

        AllAppsSearchBackground bg = findViewById(R.id.fallback_search_view);
        bg.setShadowAlpha(0);
        addElevationController(bg.getElevationController());
    }
}
