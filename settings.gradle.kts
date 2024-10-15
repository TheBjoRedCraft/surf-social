rootProject.name = "surf-social"

// Friends
include(":surf-friends")
include(":surf-friends:surf-friends-api")
include(":surf-friends:surf-friends-core")
include(":surf-friends:surf-friends-velocity")
include("surf-friends:surf-friends-paper")
findProject(":surf-friends:surf-friends-paper")?.name = "surf-friends-paper"
include("surf-friends:surf-friends-velocity")
findProject(":surf-friends:surf-friends-velocity")?.name = "surf-friends-velocity"
include("surf-friends:surf-friends-api-yml")
findProject(":surf-friends:surf-friends-api-yml")?.name = "surf-friends-api-yml"
include("surf-friends")
include("surf-friends:surf-friends-paper")
findProject(":surf-friends:surf-friends-paper")?.name = "surf-friends-paper"
include("surf-friends:surf-friends-velocity")
findProject(":surf-friends:surf-friends-velocity")?.name = "surf-friends-velocity"
include("surf-friends:surf-friends-core")
findProject(":surf-friends:surf-friends-core")?.name = "surf-friends-core"
include("surf-friends:surf-friends-api")
findProject(":surf-friends:surf-friends-api")?.name = "surf-friends-api"
include("surf-friends:surf-friends-api-fallback")
findProject(":surf-friends:surf-friends-api-fallback")?.name = "surf-friends-api-fallback"
