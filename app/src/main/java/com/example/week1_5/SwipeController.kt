import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import com.example.week1_5.contactAdapter


enum class ButtonsState {
    GONE, LEFT_VISIBLE, RIGHT_VISIBLE
}
abstract class SwipeControllerActions {
    open fun delete_contact(position: Int){}
    open fun edit_contact(position: Int){}
}





class SwipeController(val adapter: contactAdapter, val buttonsActions: SwipeControllerActions ) : ItemTouchHelper.Callback() {


    var swipeBack = false
    var isSwipeEnabled = true
    var buttonShowedState: ButtonsState = ButtonsState.GONE
    val buttonWidth = 300f
    var buttonInstance1: RectF? = null;
    var buttonInstance2: RectF? = null;
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    var mPosition:Int=-2


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            if (dX != 0f) { // 스와이프가 끝나지 않았다면 버튼을 그리도록 합니다.
                drawButtons(c, viewHolder)
            }
            super.onChildDraw(
                c!!, recyclerView!!,
                viewHolder!!, dX, dY, actionState, isCurrentlyActive
            )
        }
        setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (buttonShowedState == ButtonsState.GONE) {
            adapter.notifyItemChanged(viewHolder.adapterPosition)
            setItemsClickable(recyclerView, true)
        }
        currentItemViewHolder = viewHolder
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val buttonWidthWithoutPadding = buttonWidth - 20
        val corners = 16f
        val itemView = viewHolder.itemView
        val p = Paint()

        val leftButton = RectF(
            itemView.right - buttonWidthWithoutPadding * 2,
            itemView.top.toFloat(),
            itemView.right - buttonWidthWithoutPadding,
            itemView.bottom.toFloat()
        )
        p.color = Color.BLUE
        c.drawRoundRect(leftButton, corners, corners, p)
        drawText("EDIT", c, leftButton, p)
        val rightButton = RectF(
            itemView.right - buttonWidthWithoutPadding,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )
        p.color = Color.RED
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("DELETE", c, rightButton, p)
        buttonInstance1 = leftButton
        buttonInstance2 = rightButton
    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 60f
        p.color = Color.WHITE
        p.isAntiAlias = true
        p.textSize = textSize
        val textWidth = p.measureText(text)
        c.drawText(text, button.centerX() - textWidth / 2, button.centerY() + textSize / 2, p)
    }

    fun onDraw(c: Canvas?) {
        if (currentItemViewHolder != null && buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            drawButtons(c!!, currentItemViewHolder!!)
        }
    }

    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX < -buttonWidth) buttonShowedState =
                    ButtonsState.RIGHT_VISIBLE
                else if (dX > buttonWidth) buttonShowedState =
                    ButtonsState.LEFT_VISIBLE
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                    setTouchDownListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    setItemsClickable(recyclerView, false)
                    adapter.setItemClickable(false)
                }
                else if (buttonShowedState == ButtonsState.LEFT_VISIBLE){
                    adapter.notifyItemChanged(viewHolder.adapterPosition)
                    setItemsClickable(recyclerView, true)
                }
                if (viewHolder.adapterPosition!=-1){
                    mPosition = viewHolder.adapterPosition
                }

                Log.d("찐테","id=${mPosition}")
            }
            false
        }
    }

    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            false
        }
    }

    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {

        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                Log.d("View","${v.id}")
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    0f,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                recyclerView.setOnTouchListener { v, event -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false

                if (buttonInstance1 != null && buttonInstance1!!.contains(event.x, event.y)) {
                    Log.d("tag","왼쪽")
                    buttonsActions.edit_contact(mPosition)

                } else if (buttonInstance2 != null && buttonInstance2!!.contains(event.x, event.y)) {
                    buttonsActions.delete_contact(mPosition)
                }

                buttonShowedState = ButtonsState.GONE
            }
            false
        }
    }

    private fun setItemsClickable(
        recyclerView: RecyclerView,
        isClickable: Boolean
    ) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
        if (isClickable) {
            // 스와이프 완료 후에는 아이템 클릭 가능
            Log.d("Tag","스와이프 완료")
            adapter.setItemClickable(true)
        }
    }
}
