package sotechat.wrappers;

public class ConvInfo {

    private String channelId;
    private String date;
    private String person;
    private String category;

    /**
     * Konstruktori.
     * @param pChannelId p
     * @param pDate p
     * @param pPerson p
     * @param pCategory p
     */
    public ConvInfo(
            final String pChannelId,
            final String pDate,
            final String pPerson,
            final String pCategory
    ) {
        this.channelId = pChannelId;
        this.date = pDate;
        this.person = pPerson;
        this.category = pCategory;
    }

    public final String getChannelId() {
        return channelId;
    }

    public final String getDate() {
        return date;
    }

    public final String getPerson() {
        return person;
    }

    public String getCategory() {
        return category;
    }

}
