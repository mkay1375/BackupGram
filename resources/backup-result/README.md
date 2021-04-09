# backup-result

This directory contains files related to presenting backup file (`data.js`).

After BackupGram finished its backup process, it will copy this folder\* and backup data
(`data.js`) to selected path.

`data.js` contains a an object named `backupResult` containing _conversations_, _contacts_ and _foreign users_.

\* Well BackupGram won't copy this folder, instead it will extract the zip file
containing this folder; currently zip file of this folder must be created manually.

**Conversations**

Which can be a simple chat, a group (or supergroup) or channel; a conversation may include an array of text messages.

**Contacts**

For now, contacts always come with backup.

**Foreign Users**

These people are not your contacts (you haven't saved them as contacts), and you may not have their numbers.

## More Details

`index.html` will show conversations and contacts; the code behind it, is `index.js`.

If you click on a conversation, you will be redirected to `conversation.html` which its script is `conversation.js`.

`conversation.js` will use hash at the end of url, to know which conversation will be loaded;
other data such as page number and page size are provided in hash.

`main.css` is linked in every page and should be linked in new pages for visual consistency.

`utils.js` contains some helper functions to make life easier. 