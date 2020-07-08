db.createUser(
    {
        user: "user1",
        pwd: "secretpassword",
        roles: [
            {
                role: "root",
                db: "lmsdb"
            }
        ]
    }
);