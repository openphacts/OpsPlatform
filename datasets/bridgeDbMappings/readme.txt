Here are some notes about the bridgedb files.

in the database:

Ck - Kegg                                               
Nw - NuGo WIki                                                
Cp - PubChem                                               
Wi - Wikipedia                                               
Ca - CAS                                               
Ce - ChEBI                                               
Ch - Hmdb

To access the bridgedb directly look at:
http://www.bridgedb.org/wiki/SquirrelSql

Looking up id tables.

SELECT l.IDRIGHT as ONE, m.IDRIGHT as TWO FROM link AS l INNER JOIN link as m ON l.IDLEFT=m.IDLEFT AND l.CODERIGHT='Ck' AND m.CODERIGHT='Ce';


