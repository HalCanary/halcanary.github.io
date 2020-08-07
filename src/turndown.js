#! /usr/bin/env node
var TurndownService = require('turndown');
var fs = require("fs");

const readstdin = () => (fs.readFileSync(0).toString());

var turndownService = new TurndownService();
turndownService.keep(['img', 'table', 'div', 'span', 'input', 'form', 'script'])
var html = readstdin();
var markdown = turndownService.turndown(html);
process.stdout.write(markdown);
process.stdout.write("\n");
