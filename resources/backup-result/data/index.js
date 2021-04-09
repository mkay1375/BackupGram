$(function () {
    const PAGE_SIZE = 50;
    let $conversations = $('#conversations'),
        $contacts = $('#contacts');

    $conversations.html('');
    $contacts.html('');

    Utils.executeAsync(function () {
        $.each(backupResult.conversations, function (i, conversation) {
            createConversation(conversation.id, conversation.peerName, conversation.type, conversation.messages.length);
        })
    });

    Utils.executeAsync(function () {
        $.each(backupResult.contacts, function (i, contact) {
            createContact(contact.firstName, contact.lastName, contact.userName, contact.phone);
        })
    });


    function createConversation(conversationId, conversationName, type, messageCount) {
        let conversationTemplate =
            `<div class="col-md-6 col-lg-4 conversation">
                <div class="row">
                    <div class="col-3 text-center">
                        <img class="img-fluid w-75 mt-1" src="data/image/@type@.png">
                    </div>
                    <div class="col-9 mt-1">
                        <h6>
                            @title@
                             <span class="badge badge-secondary">@messageCount@</span>
                        </h6>
                        <p><a href="data/conversation.html#@hash@">View Messages</a></p>
                    </div>
                </div>
            </div>`;

        $conversations.append(conversationTemplate
            .replace('@type@', type.toLowerCase())
            .replace('@title@', conversationName)
            .replace('@messageCount@', messageCount)
            .replace('@hash@', $.param({cId: conversationId, page: 1, pageSize: PAGE_SIZE})));
    }

    function createContact(firstName, lastName, userName, phone) {
        let contactTemplate =
            `<div class="col-md-6 col-lg-4 contact">
                <div class="row">
                    <div class="col-3">
                        <img class="img-fluid w-75 mt-1" src="data/image/contact.png">
                    </div>
                    <div class="col-9 mt-1">
                        <p><b>Firstname:</b> @firstName@</p>
                        <p><b>Lastname:</b> @lastName@</p>
                        <p><b>Username:</b> @userName@</p>
                        <p><b>Phone:</b> @phone@</p>
                    </div>
                </div>
            </div>`;

        $contacts.append(contactTemplate
            .replace('@firstName@', firstName)
            .replace('@lastName@', lastName)
            .replace('@userName@', userName)
            .replace('@phone@', phone));
    }
});