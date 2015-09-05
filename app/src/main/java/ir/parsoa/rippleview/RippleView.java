package ir.parsoa.rippleview ;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * Created by Parsoa on 8/31/15.
 */
public class RippleView extends View
{

    private static final String DEBUG_TAG = "RippleView" ;

    private int maxRadius = 80 ;

    private float outerAlpha = 0 ;
    private float innerAlpha = 0 ;

    private float outerRadius = 0 ;
    private float innerRadius = 0 ;

    private int width ;
    private int height ;

    private int rippleColorID ;

    private static final long RIPPLE_DURATION = 300 ;

    private Path outerPath = new Path() ;
    private Paint outerPaint;

    private Path innerPath = new Path() ;
    private Paint innerPaint ;

    // ========================================================================================== \\

    public static void DrawRippleAtPosition(Activity context , int rippleCenterX , int rippleCenterY ,
                                            int rippleRadius , int colorID , ViewGroup rootLayout)
    {
        RippleView rippleView = new RippleView(context);
        rippleView.setRippleRadius(rippleRadius);
        rootLayout.addView(rippleView);
        rippleView.init(rippleCenterX , rippleCenterY , colorID) ;
    }

    public static void DrawRippleAtPosition(Activity context , int rippleCenterX , int rippleCenterY , int colorID , ViewGroup rootLayout)
    {
        RippleView rippleView = new RippleView(context);
        rootLayout.addView(rippleView);
        rippleView.init(rippleCenterX , rippleCenterY , colorID) ;
    }

    public static void DrawRippleAtPosition(Activity context , View targetView , int colorID , ViewGroup rootLayout)
    {
        RippleView rippleView = new RippleView(context);
        rootLayout.addView(rippleView);
        int[] coordinates = new int[2] ;
        targetView.getLocationInWindow(coordinates);
        rippleView.init(coordinates[0] + targetView.getWidth() / 2,
                coordinates[1] + targetView.getHeight() / 2 - getStatusBarHeight(context) , colorID) ;
    }

    private static int getStatusBarHeight(Activity context)
    {
        Rect rectangle= new Rect();
        Window window= context.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top ;
    }

    // ========================================================================================== \\

    public RippleView(Context context)
    {
        super(context);
    }

    // ========================================================================================== \\

    public void init(float rippleCenterX , float rippleCenterY , int color)
    {
        rippleColorID = color ;
        init(rippleCenterX , rippleCenterY);
    }

    public void init(float rippleCenterX , float rippleCenterY)
    {
        outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerPaint.setAlpha(100);

        innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setAlpha(100);

        width = maxRadius * 2 ;
        height = maxRadius * 2 ;

        setX(rippleCenterX - width / 2);
        setY(rippleCenterY - height / 2);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams() ;
        params.width = width ;
        params.height = height ;
        this.setLayoutParams(params);

        startAnimation();
    }

    private void startAnimation()
    {
        OuterRippleAnimator outerRippleAnimator = new OuterRippleAnimator() ;
        outerRippleAnimator.setDuration(RIPPLE_DURATION);
        startAnimation(outerRippleAnimator);

        InnerRippleAnimator innerRippleAnimator = new InnerRippleAnimator() ;
        innerRippleAnimator.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                ((ViewGroup) getParent()).removeView(RippleView.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        innerRippleAnimator.setDuration(RIPPLE_DURATION);
        innerRippleAnimator.setStartOffset(RIPPLE_DURATION / 4);
        startAnimation(innerRippleAnimator);
    }

    private void setRadius(Paint paint , float radius , int color , float alphaFactor)
    {
        if(outerRadius > 0)
        {
            RadialGradient radialGradient = new RadialGradient(width / 2 , height / 2 , radius,
                    adjustAlpha(getResources().getColor(color), alphaFactor),
                    getResources().getColor(color), Shader.TileMode.MIRROR);
            paint.setShader(radialGradient);
        }
        else
        {
            RadialGradient radialGradient = new RadialGradient(width / 2 , height / 2 , 1 ,
                    adjustAlpha(getResources().getColor(color), alphaFactor),
                    getResources().getColor(color), Shader.TileMode.MIRROR);
            paint.setShader(radialGradient);
        }
    }

    public int adjustAlpha(int color, float factor)
    {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    // ========================================================================================== \\

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.save(Canvas.CLIP_SAVE_FLAG);

        outerPath.reset();
        outerPath.addCircle(getWidth() / 2, getHeight() / 2, outerRadius, Path.Direction.CW);
        canvas.clipPath(outerPath);

        innerPath.reset();
        innerPath.addCircle(getWidth() / 2, getHeight() / 2, innerRadius, Path.Direction.CW);
        canvas.clipPath(innerPath);

        canvas.restore() ;

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, outerRadius, outerPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, innerRadius, innerPaint);
    }

    // ========================================================================================== \\

    public class OuterRippleAnimator extends Animation
    {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            super.applyTransformation(interpolatedTime, t);

            outerAlpha = interpolatedTime * 255.f ;
            outerRadius = interpolatedTime * maxRadius ;

            setRadius(outerPaint, outerRadius , rippleColorID , interpolatedTime);
            invalidate();
        }

        @Override
        public boolean willChangeBounds() {
            return false;
        }

        @Override
        public boolean willChangeTransformationMatrix() {
            return false;
        }

    }

    // ========================================================================================== \\

    public class InnerRippleAnimator extends Animation
    {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            super.applyTransformation(interpolatedTime, t);

            innerAlpha = interpolatedTime * 255.f ;
            innerRadius = interpolatedTime * maxRadius ;

            setRadius(innerPaint, innerRadius, rippleColorID , interpolatedTime);
            invalidate();
        }

        @Override
        public boolean willChangeBounds() {
            return false;
        }

        @Override
        public boolean willChangeTransformationMatrix() {
            return false;
        }
    }

    public void setRippleRadius(int radius)
    {
        maxRadius = radius ;
    }

}
