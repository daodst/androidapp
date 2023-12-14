package im.vector.app.features.home

interface ImLoading {

    
    fun renderState(show: Boolean, text: String, progress: Int)

    
    fun imShowOrHideLoading(show: Boolean,msg :String)
}
