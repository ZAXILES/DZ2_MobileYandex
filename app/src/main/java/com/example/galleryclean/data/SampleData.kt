package com.example.galleryclean.data
import com.example.galleryclean.model.Picture

object SampleData {
    fun generateSamplePictures(): List<Picture> = listOf(
        Picture(1, "Alice", "https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?w=800&auto=format&fit=crop"),
        Picture(2, "Bob", "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=800&auto=format&fit=crop"),
        Picture(3, "Charlie", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=800&auto=format&fit=crop"),
        Picture(4, "Diana", "https://images.unsplash.com/photo-1520813792240-56fc4a3765a7?w=800&auto=format&fit=crop"),
        Picture(5, "Eve", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800&auto=format&fit=crop"),
        Picture(6, "Frank", "https://images.unsplash.com/photo-1500048993953-d23a436266cf?w=800&auto=format&fit=crop"),
        Picture(7, "Grace", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop")
    )
}