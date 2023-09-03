package ru.practicum.shareit.item;

public class ItemCommentQueries {

    public final static String queryForItemBookingDto =
            "WITH dto AS (SELECT i.id, i.name, i.description, " +
            "i.is_available AS available, i.owner_id AS ownerId " +
            "FROM items AS i WHERE i.owner_id = ?1), " +

            "dtolb AS (SELECT dto.*, lb.lastBookingId, " +
            "lb.booker_id AS lastBookerId, lb.rank_lb FROM dto " +
            "FULL JOIN (SELECT b.id as lastBookingId, b.booker_id, b.item_id, " +
            "RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date DESC) " +
            "AS rank_lb FROM bookings AS b WHERE b.status = 'APPROVED' " +
            "AND b.start_date <= now()) AS lb ON dto.id = lb.item_id) " +

            "SELECT dtolb.id, dtolb.name, dtolb.description, dtolb.available, dtolb.ownerId, " +
            "dtolb.lastBookingId, dtolb.lastBookerId, nb.id AS nextBookingId, " +
            "nb.booker_id AS nextBookerId FROM dtolb FULL JOIN (SELECT b.id, b.booker_id, " +
            "b.item_id, RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date ASC) AS rank_nb " +
            "FROM bookings AS b WHERE b.status = 'APPROVED' AND b.start_date >= now()) AS nb " +
            "ON dtolb.id = nb.item_id WHERE (dtolb.rank_lb = 1 OR dtolb.rank_lb IS NULL) " +
            "AND (nb.rank_nb = 1 OR nb.rank_nb is NULL) " +
            "ORDER BY dtolb.id ASC";

    public final static String queryForIBDByItemId =
            "WITH dto AS (SELECT i.id, i.name, i.description, " +
            "i.is_available AS available, i.owner_id AS ownerId " +
            "FROM items AS i WHERE i.id = ?1), " +

            "dtolb AS (SELECT dto.*, lb.lastBookingId, " +
            "lb.booker_id AS lastBookerId, lb.rank_lb FROM dto " +
            "FULL JOIN (SELECT b.id as lastBookingId, b.booker_id, b.item_id, " +
            "RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date DESC) " +
            "AS rank_lb FROM bookings AS b WHERE b.item_id = ?1 AND b.status = 'APPROVED' " +
            "AND b.start_date <= now()) AS lb ON dto.id = lb.item_id) " +

            "SELECT dtolb.id, dtolb.name, dtolb.description, dtolb.available, dtolb.ownerId, " +
            "dtolb.lastBookingId, dtolb.lastBookerId, nb.id AS nextBookingId, " +
            "nb.booker_id AS nextBookerId FROM dtolb FULL JOIN (SELECT b.id, b.booker_id, " +
            "b.item_id, RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date ASC) AS rank_nb " +
            "FROM bookings AS b WHERE b.item_id = ?1 AND b.status = 'APPROVED' AND b.start_date >= now()) AS nb " +
            "ON dtolb.id = nb.item_id WHERE (dtolb.rank_lb = 1 OR dtolb.rank_lb IS NULL) " +
            "AND (nb.rank_nb = 1 OR nb.rank_nb is NULL) " +
            "ORDER BY dtolb.id ASC";

    public final static String queryCommentDto = "SELECT c.id AS id, c.text AS text, " +
            "c.created AS created, u.name AS authorName " +
            "FROM Comments AS c " +
            "JOIN Users AS u ON c.author_id=u.id " +
            "WHERE c.item_id = ?1 " +
            "ORDER BY c.created DESC ";

    public final static String queryCommentItemDto = "SELECT c.id AS id, c.text AS text, " +
            "c.created AS created, u.name AS authorName, c.item_id AS itemID " +
            "FROM Comments AS c " +
            "JOIN Users AS u ON c.author_id=u.id " +
            "WHERE c.item_id IN :id " +
            "ORDER BY c.created DESC ";
}
