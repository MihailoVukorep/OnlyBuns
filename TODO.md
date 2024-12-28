# must do:
- delete account cron job after some time
- admin/accounts search needs paging
- admin analytics
- send email to inactive users
- load balancer -- https://medium.com/@apurvaagrawal_95485/load-balancing-algorithms-01d86a2a48c7
- activity monitor Prometheus/Grafana
    - HTTP ms createPost
    - CPU % usage for one timespan
    - currently active users in 24h
- sending info with protobuf or self-made buf from another apps
- OpenAPI
- Caching ; /posts cache images ; createpost cache location ; 
- ADs
- finish chat app - make it live with websockets
- remove all exceptions -- use web error codes

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

