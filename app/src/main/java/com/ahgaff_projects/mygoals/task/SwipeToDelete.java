package com.ahgaff_projects.mygoals.task;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.R;

//First, let’s create a new class that extends ItemTouchHelper.SimpleCallback and write the constructor.
public class SwipeToDelete extends ItemTouchHelper.SimpleCallback {

    private TaskRecyclerViewAdapter mAdapter;

    private Drawable icon;
    private final ColorDrawable background;

    public SwipeToDelete(TaskRecyclerViewAdapter adapter) {
        //The fist parameter in super adds support for dragging the RecyclerView item up or down. We don’t care about that hence the 0. The second parameter tells the ItemTouchHelper (that we will attach to the RecyclerView later) to pass our SimpleCallback information about left and right swipes. The constructor takes a reference to our adapter which we will use to call the deleteItem() function.
        super(0, ItemTouchHelper.START/* | ItemTouchHelper.RIGHT*/);
        this.mAdapter = adapter;

        //Optional Step: Adding the background and Icon//To add our icon and background we just need to override onChildDraw(). This will draw our icon and background in the correct position as our RecyclerView item is swiped across the screen.        //Since onChildDraw() is called multiple times per second, let’s try to keep the code as sparse as possible by adding the icon and background as member variables.
        icon = ContextCompat.getDrawable(mAdapter.context, R.drawable.delete_28);
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    //Next, we need to override onSwiped(). This is called when an item is swiped off the screen. You also need to override onMove() , but you can leave it as is.
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAbsoluteAdapterPosition();
        mAdapter.deleteItem(position);//The deleteItem() method does not exist, but we implement it later in the adapter.
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 30;

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2-15;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right for arabic
            int iconLeft = itemView.getLeft() + iconMargin ;
            int iconRight = itemView.getLeft() + iconMargin+ icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getLeft() + 20, itemView.getTop() + 14,
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom() - 14);
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop() + 14, itemView.getRight() - 20, itemView.getBottom() - 14);//we here add and subtract 14 to make the red background fit the cardView height
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }
}
