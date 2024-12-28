# must do:
## SIMPLE?
- delete account cron job after some time
- sending info with protobuf or self-made buf from another apps
- send email to inactive users
- OpenAPI
- Caching ; /posts cache images ; createpost cache location ; 
- remove all exceptions -- use web error codes
## CHAT:
- live chat done?
- send message when user joins/leaves chat
- chat admin: delete chat / add account / remove account
- make users be able to leave chat
## ADMIN:
- admin/accounts search needs paging
- admin analytics
## IDK
- load balancer -- https://medium.com/@apurvaagrawal_95485/load-balancing-algorithms-01d86a2a48c7
- activity monitor Prometheus/Grafana
    - HTTP ms createPost
    - CPU % usage for one timespan
    - currently active users in 24h
- ADs

# to fix:
- add all lombok to DTOs
- use ratelimiter class to limit follow / reply ??
- when counting things: dont do collection.size() call repository.countSomething (ex. rep.countLikes...) - WHY: since .size() is int it won't overflow on large numbers




# OPTIONAL STUFF:
## NEW CSS NEEDED:
- popup
- comment form
- create/edit post

## look:
- fix error page navbar
- fix error page buttons

## could be nice:
- delete post frontend - confirmation dialogue (YES/NO)
- reply - add picture
- create / edit add ENTER evets to textbox
- all buttons need title="" with proper tooltip
- remove main.js if not needed

