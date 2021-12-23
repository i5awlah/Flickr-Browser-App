package com.example.flickrbrowserapp.models.xmlModel
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "rsp", strict = false)
class RssFeed: Serializable {
    @field:Element(required = false, name = "photos")
    var photos: Photos? = null
}

@Root(name = "photos", strict = false)
class Photos: Serializable {
    @field:ElementList(inline = true, name = "photo")
    var photo: List<Photo>? = null
}


@Root(name = "photo", strict = false)
class Photo: Serializable {
    @field:Attribute(name = "id", required = true)
    var id: String? = null
    @field:Attribute(name = "secret", required = true)
    var secret: String? = null
    @field:Attribute(name = "server", required = true)
    var server: String? = null
    @field:Attribute(name = "title", required = true)
    var title: String? = null
}