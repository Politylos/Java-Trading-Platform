/**
 * Used as the int for the server to know what to do with each pack class when praising their number id
 */
public enum Server_Code {
    LOGIN(10),
    CHANGE_PASSWORD(11),
    VIEW_INV(12),
    GET_MARKET(20),
    SELL(21),
    BUY(22),
    GET_ASSET(23),
    ASSET_LISTING(24),
    ADD_USER(30),
    ADD_OU(31),
    ADD_ASSET(32),
    ADD_OU_ASSET(33),
    ADD_CREDITS(34),
    REMOVE_USER(35),
    REMOVE_OU(36),
    REMOVE_ASSET(37),
    UPDATE_PASSWORD_ADMIN(40),
    UPDATE_USER_ORG(41),
    UPDATEUSER_TYPE(42),
    REMOVE_TRADE(25),
    CLOSE(43),
    CHECK_TRADE(44),
    OU_ADD_CREDITS(34),
    FETCH_USERS(45)
    ;


    private final int id;
    Server_Code(int i) {
        this.id = i;
    }

    public int getId() {
        return id;
    }
}
