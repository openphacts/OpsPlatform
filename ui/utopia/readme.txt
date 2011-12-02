This is to run the Utopia integration with pdsp and chemspider

- Install utopia (you can get this from http://getutopia.com/)
_ Place the contents of this directory in the utopia python plugins directory
-- On a mac you can create the directory in ~/Library/Utopia/plugins/python and put the files there
- Make sure larkc is running
- launch the workflow datasetsworkflow.ttl in the larkc-workflow directory
- make sure that this is the first workflow you launch so it's running on http://localhost:8183

- Launch Utopia and load the pdf file pubmed19520572.pdf

- You should see utopia doing stuff
- If you go down to the bottom on the pdf file you'll see a utopia annotation 
- click on it and you should see a series of chemicals on the right side with ki values

- make sure you run this on a good internet connection otherwise the connection to chemspider won't be fast enough
