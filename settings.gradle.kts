rootProject.name = "surf-social"

// Friends
include(":surf-friends")
include(":surf-friends:surf-friends-api")
include(":surf-friends:surf-friends-core")
include(":surf-friends:surf-friends-velocity")
include("surf-friends:surf-friends-paper")
findProject(":surf-friends:surf-friends-paper")?.name = "surf-friends-paper"
