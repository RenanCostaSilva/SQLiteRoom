import android.view.View

fun View.esconder(){
  if(visibility == View.VISIBLE){
      visibility = View.GONE
  }
}

fun View.mostrar(){
    if(visibility == View.GONE){
        visibility = View.VISIBLE
    }
}