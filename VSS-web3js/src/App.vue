<template>
    <div id="app">
            <!--UPLOAD-->
                <h1>Notarize PDF</h1>
                <div class="dropbox">
                    <input type="file" @change="filesChange($event.target.files);" class="input-file">
                    Drag your file(s) here to begin<br> or click to browse<br>
                    <div v-if="hash !== null">SHA256 Hash: <b>{{hash}}</b></div>
                    <div v-if="fileName !== null">Name: <b>{{fileName}}</b></div>
                    <div v-if="timestamp !== 0"><span id="check">&#10003;</span> VERIFIED at timestamp {{timestamp}}s with account {{web3.eth.defaultAccount}}</div>

                </div>
                <button @click="store()" :disabled="isDisabled">Notarize</button> status: <span v-html="status"></span>
    </div>
</template>

<script>
    const utils = require('./utils.js')

    export default {
        data: function () {
            return {
                web3: null,
                contract: null,
                hash: null,
                status: 'nothing uploaded',
                isDisabled: true,
                fileName: null,
                timestamp: 0
            }
        },
        created: function () {
            window.addEventListener('load', () => {
                // Checking if Web3 has been injected by the browser (Mist/MetaMask)
                if (typeof web3 !== 'undefined') {
                    // Use Mist/MetaMask's provider
                    this.web3 = new Web3(web3.currentProvider);
                    const address = "0xadf01c7efda5fb673ec9f2cf3e1c78e7cde9aaac";
                    this.contract = this.web3.eth.contract([{"constant":true,"inputs":[{"name":"recipient","type":"address"},{"name":"hash","type":"bytes32"}],"name":"verify","outputs":[{"name":"","type":"uint256"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"hash","type":"bytes32"}],"name":"store","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"}]).at(address);
                } else {
                    this.status = 'No web3? You should consider trying MetaMask!'
                }
            })
        },
        methods: {
            filesChange(fileList) {
                const fr = new FileReader();
                fr.onload = (e) => {
                    this.hash = utils.stringHash(e.target.result);
                    console.log('hash is: '+this.hash)
                    this.contract.verify(this.web3.eth.defaultAccount, this.hash, (error, result) => {
                        if(!error) {
                            if(result == 0) {
                                this.isDisabled = false
                                this.timestamp = 0
                                this.status = 'not yet stored from accout: '+this.web3.eth.defaultAccount
                            } else {
                                this.isDisabled = true
                                this.timestamp = parseInt(result)
                                this.status = '<b>VERIFIED</b> in the blockchain!'
                            }
                        } else {
                            this.timestamp = 0
                            this.isDisabled = false
                            this.status = 'error: '+error
                        }
                    });
                }
                fr.readAsArrayBuffer(fileList[0]);
                this.fileName = fileList[0].name
            },
            store() {
                this.contract.store(this.hash, (error, result) => {
                    if(!error) {
                        this.status = 'stored, tx is: '+result.toString()
                    } else {
                        this.status = 'error: '+error;
                    }
                });
                this.timestamp = 0
                this.isDisabled = true
            }
        }
    }
</script>

<style>
    .dropbox {
        outline: 2px dashed grey; /* the dash box */
        outline-offset: -10px;
        background: lightcyan;
        color: dimgray;
        padding: 10px 10px;
        min-height: 200px; /* minimum height */
        position: relative;
        cursor: pointer;
    }

    .input-file {
        opacity: 0; /* invisible but it's there! */
        width: 100%;
        height: 200px;
        position: absolute;
        cursor: pointer;
    }

    .dropbox:hover {
        background: lightblue; /* when mouse over to the drop zone, change color */
    }

    .dropbox p {
        font-size: 1.2em;
        text-align: center;
        padding: 50px 0;
    }

    #check {
        content: "\2713";
        color:green;
        font-size: 2em;
    }
</style>