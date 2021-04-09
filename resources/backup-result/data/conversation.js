$(function () {
    const ViewModes = {CONTINUOUS: 0, PAGING: 1};
    const OrderModes = {NEW_TO_OLD: 0, OLD_TO_NEW: 1};
    const SelectedButtonClasses = ['selected', 'default-cursor'];

    let $messages = $('#messages'),
        $pages = $('#pages'),
        $continuousMode = $('#continuous-mode'),
        $pageMode = $('#page-mode'),
        $oldToNew = $('#old-to-new'),
        $newToOld = $('#new-to-old'),
        viewMode,
        orderMode;

    $continuousMode.attr('data-first-page-hash', window.location.hash);
    setUpViewOptions();

    window.onhashchange = main;

    function main() {
        $pages.html('');

        let params = Utils.deparam(window.location.hash.substr(1)),
            currentUserId = backupResult.currentUserId,
            currentConversation = findCurrentConversation(params.cId);

        $('#current-conversation-image').attr('src', 'image/' + currentConversation.type.toLowerCase() + '.png');
        $('#current-conversation-name').text(currentConversation.peerName);
        document.title = 'BackupGram - ' + currentConversation.peerName;

        showPages(params, currentConversation.messages.length, params.cId);

        $messages.html('');
        showPageOfMessages(currentUserId, currentConversation, params.page, params.pageSize);

        let p = 1,
            scrollBottomDate = new Date().getTime(),
            numberOfPages = Math.ceil(currentConversation.messages.length / params.pageSize);
        window.onscroll = function () {
            if (viewMode === ViewModes.CONTINUOUS
                && document.body.scrollHeight <= window.scrollY + window.innerHeight + 1000
                && scrollBottomDate < new Date().getTime() - 100
                && params.page + p <= numberOfPages) {
                showPageOfMessages(currentUserId, currentConversation, params.page + p++, params.pageSize);
                scrollBottomDate = new Date().getTime();
            }
        };
    }

    function showPageOfMessages(currentUserId, currentConversation, page, pageSize) {
        let messagesLength = currentConversation.messages.length,
            beginFrom,
            endTo,
            direction;
        if (orderMode === OrderModes.NEW_TO_OLD) {
            beginFrom = (page - 1) * pageSize;
            endTo = beginFrom + pageSize;
            if (endTo >= messagesLength) endTo = messagesLength;
            direction = 1;
        } else if (orderMode === OrderModes.OLD_TO_NEW) {
            beginFrom = (messagesLength - 1) - ((page - 1) * pageSize);
            endTo = beginFrom - pageSize;
            if (endTo < 0) endTo = -1;
            direction = -1;
        }

        for (let i = beginFrom; i !== endTo; i += direction) {
            let message = currentConversation.messages[i];
            createMessage(currentUserId, message.fromId, message.toId, message.date, message.message, currentConversation.type === 'CHANNEL');
        }
    }

    function createMessage(currentUserId, fromId, toId, date, message, isChannel) {
        message = message.replace('\n', '<br>');
        let sender;
        let contactShortName = '';
        if (!isChannel) {
            sender = findSender(fromId);
            if (sender !== null) {
                if (sender.firstName !== '')
                    contactShortName += sender.firstName[0].toUpperCase();
                if (sender.lastName !== '')
                    contactShortName += sender.lastName[0].toUpperCase();
            }
        }

        let inMessageTemplate =
            `<div class="row">
                <div class="col-md-5 col-9">
                    <div class="contact in not-selectable">@contactShortName@</div>
                    <div class="message in">
                        <div class="message-text">@message@</div>
                        <span class="date">@date@</span>
                    </div>
                </div>
            </div>`;
        let outMessageTemplate =
            `<div class="row">
                <div class="offset-md-7 offset-3 col-md-5 col-9">
                    <div class="contact out not-selectable">You</div>
                    <div class="message out">
                        <div class="text-right message-text">@message@</div>
                        <div class="text-right date">@date@</div>
                    </div>
                </div>
            </div>`;

        if (fromId == currentUserId) {
            $messages.append(outMessageTemplate
                .replace('@message@', message)
                .replace('@date@', new Date(date * 1000).toLocaleString()));
        } else {
            $messages.append(inMessageTemplate
                .replace('@message@', message)
                .replace('@contactShortName@', contactShortName)
                .replace('@date@', new Date(date * 1000).toLocaleString()));
        }

        let contactPopoverTemplate = `
            <div>
                <p><b>Firstname:</b> @firstName@</p>
                <p><b>Lastname:</b> @lastName@</p>
                <p><b>Username:</b> @userName@</p>
                <p><b>Phone:</b> @phone@</p>
            </div>`;

        if (sender) {
            $('.contact').popover({
                container: 'body',
                title: `
                <div class="text-center">@title@</div>
            `.replace('@title@', sender.firstName + ' ' + sender.lastName),
                content: contactPopoverTemplate
                    .replace('@firstName@', sender.firstName)
                    .replace('@lastName@', sender.lastName)
                    .replace('@userName@', sender.userName)
                    .replace('@phone@', sender.phone),
                html: true
            });
        } else {
            $('.contact').css('display', 'none');
        }
    }

    function showPages(params, messagesCount, conversationId) {
        let currentPage = params.page,
            pageSize = params.pageSize,
            numberOfPages = Math.ceil(messagesCount / pageSize);

        let pageTemplate =
            `<div class="col-md-2 col-6 order-1 mb-2">
                <a href="#@previousPageHash@" class="btn btn-primary w-100 @previousPageClass@">@previousPageText@</a>
            </div>
            <div class="col-md-8 order-0 order-md-1 mb-2" style="text-align: center">
                Page
                <input id="page-input" type="number" class="form-control d-inline" style="width: 4em; text-align: center;"
                       max="@numberOfPages@" min="1" value="@currentPage@"/>
                of
                @numberOfPages@
                <a id="goto-page" href="#@currentPageHash@" class="btn btn-success" style="visibility: hidden">Go</a>
            </div>
            <div class="col-md-2 col-6 order-2 order-md-2 mb-2">
                <a href="#@nextPageHash@" class="btn btn-primary w-100 @nextPageClass@">@nextPageText@</a>
            </div>`;


        $pages.append(pageTemplate
            .replace('@previousPageHash@', $.param({cId: conversationId, page: currentPage - 1, pageSize: pageSize}))
            .replace('@previousPageClass@', currentPage === 1 ? 'disabled' : '')
            .replace('@numberOfPages@', numberOfPages.toString())
            .replace('@numberOfPages@', numberOfPages.toString()) // We have two of them
            .replace('@currentPage@', currentPage.toString())
            .replace('@currentPageHash@', $.param({cId: conversationId, page: currentPage, pageSize: pageSize}))
            .replace('@nextPageHash@', $.param({cId: conversationId, page: currentPage + 1, pageSize: pageSize}))
            .replace('@nextPageClass@', currentPage === numberOfPages ? 'disabled' : '')
            .replace('@previousPageText@', orderMode === OrderModes.OLD_TO_NEW ? 'Older' : 'Newer')
            .replace('@nextPageText@', orderMode === OrderModes.OLD_TO_NEW ? 'Newer' : 'Older')
        );

        let $pageInput = $('#page-input'),
            $gotoPage = $('#goto-page');
        $pageInput.on('keyup', function (e) {
            if (+$pageInput.val() > +$pageInput.attr('max'))
                $gotoPage.css('visibility', 'hidden');
            else if (+$pageInput.val() < +$pageInput.attr('min'))
                $gotoPage.css('visibility', 'hidden');
            else {
                if (e.keyCode === 13) {
                    window.location.hash = $gotoPage.attr('href');
                } else {
                    params.page = +$pageInput.val();
                    $gotoPage.css('visibility', 'visible').attr('href', '#@'.replace('@', $.param(params)));
                }
            }
        })
    }

    function findCurrentConversation(conversationId) {
        for (let i = 0; i < backupResult.conversations.length; i++) {
            if (backupResult.conversations[i].id == conversationId)
                return backupResult.conversations[i];
        }
    }

    function findSender(senderId) {
        for (let i = 0; i < backupResult.contacts.length; i++) {
            let contact = backupResult.contacts[i];
            if (contact.id == senderId)
                return contact;
        }
        for (let i = 0; i < backupResult.foreignUsers.length; i++) {
            let contact = backupResult.foreignUsers[i];
            if (contact.id == senderId)
                return contact;
        }
        return null;
    }

    function setUpViewOptions() {
        $continuousMode.on('click', function () {
            viewMode = ViewModes.CONTINUOUS;
            migrateClasses(SelectedButtonClasses, $pageMode, $continuousMode);
            $pages.css('display', 'none');
            window.location.hash = $continuousMode.attr('data-first-page-hash');
        });

        $continuousMode.click(); // Set continuous mode as default

        $pageMode.on('click', function () {
            viewMode = ViewModes.PAGING;
            migrateClasses(SelectedButtonClasses, $continuousMode, $pageMode);
            $pages.css('display', '');
        });

        $newToOld.on('click', function () {
            orderMode = OrderModes.NEW_TO_OLD;
            migrateClasses(SelectedButtonClasses, $oldToNew, $newToOld);
            main();
        });


        $oldToNew.on('click', function () {
            orderMode = OrderModes.OLD_TO_NEW;
            migrateClasses(SelectedButtonClasses, $newToOld, $oldToNew);
            main();
        });

        $oldToNew.click(); // Set as default
    }

    function migrateClasses(classes, from, to) {
        classes.forEach(cl => {
            from.removeClass(cl);
            to.addClass(cl);
        })
    }
});