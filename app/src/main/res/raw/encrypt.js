function RSAKeyPair(i,t,r){this.e=biFromHex(i),this.d=biFromHex(t),this.m=biFromHex(r),this.chunkSize=2*biHighIndex(this.m),this.radix=16,this.barrett=new BarrettMu(this.m)}function twoDigit(i){return(i<10?"0":"")+String(i)}function encryptedString(i,t){for(var r=new Array,e=t.length,g=0;g<e;)r[g]=t.charCodeAt(g),g++;for(;r.length%i.chunkSize!=0;)r[g++]=0;var n,s,d,a=r.length,o="";for(g=0;g<a;g+=i.chunkSize){for(d=new BigInt,n=0,s=g;s<g+i.chunkSize;++n)d.digits[n]=r[s++],d.digits[n]+=r[s++]<<8;var u=i.barrett.powMod(d,i.e);o+=(16==i.radix?biToHex(u):biToString(u,i.radix))+" "}return o.substring(0,o.length-1)}function decryptedString(i,t){var r,e,g,n=t.split(" "),s="";for(r=0;r<n.length;++r){var d;for(d=16==i.radix?biFromHex(n[r]):biFromString(n[r],i.radix),g=i.barrett.powMod(d,i.d),e=0;e<=biHighIndex(g);++e)s+=String.fromCharCode(255&g.digits[e],g.digits[e]>>8)}return 0==s.charCodeAt(s.length-1)&&(s=s.substring(0,s.length-1)),s}var maxDigits,ZERO_ARRAY,bigZero,bigOne,biRadixBase=2,biRadixBits=16,bitsPerDigit=biRadixBits,biRadix=65536,biHalfRadix=biRadix>>>1,biRadixSquared=biRadix*biRadix,maxDigitVal=biRadix-1,maxInteger=9999999999999998;function setMaxDigits(i){ZERO_ARRAY=new Array(maxDigits=i);for(var t=0;t<ZERO_ARRAY.length;t++)ZERO_ARRAY[t]=0;bigZero=new BigInt,(bigOne=new BigInt).digits[0]=1}setMaxDigits(20);var dpl10=15,lr10=biFromNumber(1e15);function BigInt(i){this.digits="boolean"==typeof i&&1==i?null:ZERO_ARRAY.slice(0),this.isNeg=!1}function biFromDecimal(i){for(var t,r="-"==i.charAt(0),e=r?1:0;e<i.length&&"0"==i.charAt(e);)++e;if(e==i.length)t=new BigInt;else{var g=(i.length-e)%dpl10;for(0==g&&(g=dpl10),t=biFromNumber(Number(i.substr(e,g))),e+=g;e<i.length;)t=biAdd(biMultiply(t,lr10),biFromNumber(Number(i.substr(e,dpl10)))),e+=dpl10;t.isNeg=r}return t}function biCopy(i){var t=new BigInt(!0);return t.digits=i.digits.slice(0),t.isNeg=i.isNeg,t}function biFromNumber(i){var t=new BigInt;t.isNeg=i<0,i=Math.abs(i);for(var r=0;0<i;)t.digits[r++]=i&maxDigitVal,i=Math.floor(i/biRadix);return t}function reverseStr(i){for(var t="",r=i.length-1;-1<r;--r)t+=i.charAt(r);return t}var hexatrigesimalToChar=new Array("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z");function biToString(i,t){var r=new BigInt;r.digits[0]=t;for(var e=biDivideModulo(i,r),g=hexatrigesimalToChar[e[1].digits[0]];1==biCompare(e[0],bigZero);)e=biDivideModulo(e[0],r),digit=e[1].digits[0],g+=hexatrigesimalToChar[e[1].digits[0]];return(i.isNeg?"-":"")+reverseStr(g)}function biToDecimal(i){var t=new BigInt;t.digits[0]=10;for(var r=biDivideModulo(i,t),e=String(r[1].digits[0]);1==biCompare(r[0],bigZero);)r=biDivideModulo(r[0],t),e+=String(r[1].digits[0]);return(i.isNeg?"-":"")+reverseStr(e)}var hexToChar=new Array("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f");function digitToHex(t){var r="";for(i=0;i<4;++i)r+=hexToChar[15&t],t>>>=4;return reverseStr(r)}function biToHex(i){for(var t="",r=(biHighIndex(i),biHighIndex(i));-1<r;--r)t+=digitToHex(i.digits[r]);return t}function charToHex(i){return 48<=i&&i<=57?i-48:65<=i&&i<=90?10+i-65:97<=i&&i<=122?10+i-97:0}function hexToDigit(i){for(var t=0,r=Math.min(i.length,4),e=0;e<r;++e)t<<=4,t|=charToHex(i.charCodeAt(e));return t}function biFromHex(i){for(var t=new BigInt,r=i.length,e=0;0<r;r-=4,++e)t.digits[e]=hexToDigit(i.substr(Math.max(r-4,0),Math.min(r,4)));return t}function biFromString(i,t){var r="-"==i.charAt(0),e=r?1:0,g=new BigInt,n=new BigInt;n.digits[0]=1;for(var s=i.length-1;e<=s;s--){g=biAdd(g,biMultiplyDigit(n,charToHex(i.charCodeAt(s)))),n=biMultiplyDigit(n,t)}return g.isNeg=r,g}function biDump(i){return(i.isNeg?"-":"")+i.digits.join(" ")}function biAdd(i,t){var r;if(i.isNeg!=t.isNeg)t.isNeg=!t.isNeg,r=biSubtract(i,t),t.isNeg=!t.isNeg;else{r=new BigInt;for(var e,g=0,n=0;n<i.digits.length;++n)e=i.digits[n]+t.digits[n]+g,r.digits[n]=e%biRadix,g=Number(biRadix<=e);r.isNeg=i.isNeg}return r}function biSubtract(i,t){var r;if(i.isNeg!=t.isNeg)t.isNeg=!t.isNeg,r=biAdd(i,t),t.isNeg=!t.isNeg;else{var e,g;r=new BigInt;for(var n=g=0;n<i.digits.length;++n)e=i.digits[n]-t.digits[n]+g,r.digits[n]=e%biRadix,r.digits[n]<0&&(r.digits[n]+=biRadix),g=0-Number(e<0);if(-1==g){for(n=g=0;n<i.digits.length;++n)e=0-r.digits[n]+g,r.digits[n]=e%biRadix,r.digits[n]<0&&(r.digits[n]+=biRadix),g=0-Number(e<0);r.isNeg=!i.isNeg}else r.isNeg=i.isNeg}return r}function biHighIndex(i){for(var t=i.digits.length-1;0<t&&0==i.digits[t];)--t;return t}function biNumBits(i){var t,r=biHighIndex(i),e=i.digits[r],g=(r+1)*bitsPerDigit;for(t=g;g-bitsPerDigit<t&&0==(32768&e);--t)e<<=1;return t}function biMultiply(i,t){for(var r,e,g,n=new BigInt,s=biHighIndex(i),d=biHighIndex(t),a=0;a<=d;++a){for(r=0,g=a,j=0;j<=s;++j,++g)e=n.digits[g]+i.digits[j]*t.digits[a]+r,n.digits[g]=e&maxDigitVal,r=e>>>biRadixBits;n.digits[a+s+1]=r}return n.isNeg=i.isNeg!=t.isNeg,n}function biMultiplyDigit(i,t){var r,e,g;result=new BigInt,r=biHighIndex(i);for(var n=e=0;n<=r;++n)g=result.digits[n]+i.digits[n]*t+e,result.digits[n]=g&maxDigitVal,e=g>>>biRadixBits;return result.digits[1+r]=e,result}function arrayCopy(i,t,r,e,g){for(var n=Math.min(t+g,i.length),s=t,d=e;s<n;++s,++d)r[d]=i[s]}var highBitMasks=new Array(0,32768,49152,57344,61440,63488,64512,65024,65280,65408,65472,65504,65520,65528,65532,65534,65535);function biShiftLeft(i,t){var r=Math.floor(t/bitsPerDigit),e=new BigInt;arrayCopy(i.digits,0,e.digits,r,e.digits.length-r);for(var g=t%bitsPerDigit,n=bitsPerDigit-g,s=e.digits.length-1,d=s-1;0<s;--s,--d)e.digits[s]=e.digits[s]<<g&maxDigitVal|(e.digits[d]&highBitMasks[g])>>>n;return e.digits[0]=e.digits[s]<<g&maxDigitVal,e.isNeg=i.isNeg,e}var lowBitMasks=new Array(0,1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,32767,65535);function biShiftRight(i,t){var r=Math.floor(t/bitsPerDigit),e=new BigInt;arrayCopy(i.digits,r,e.digits,0,i.digits.length-r);for(var g=t%bitsPerDigit,n=bitsPerDigit-g,s=0,d=s+1;s<e.digits.length-1;++s,++d)e.digits[s]=e.digits[s]>>>g|(e.digits[d]&lowBitMasks[g])<<n;return e.digits[e.digits.length-1]>>>=g,e.isNeg=i.isNeg,e}function biMultiplyByRadixPower(i,t){var r=new BigInt;return arrayCopy(i.digits,0,r.digits,t,r.digits.length-t),r}function biDivideByRadixPower(i,t){var r=new BigInt;return arrayCopy(i.digits,t,r.digits,0,r.digits.length-t),r}function biModuloByRadixPower(i,t){var r=new BigInt;return arrayCopy(i.digits,0,r.digits,0,t),r}function biCompare(i,t){if(i.isNeg!=t.isNeg)return 1-2*Number(i.isNeg);for(var r=i.digits.length-1;0<=r;--r)if(i.digits[r]!=t.digits[r])return i.isNeg?1-2*Number(i.digits[r]>t.digits[r]):1-2*Number(i.digits[r]<t.digits[r]);return 0}function biDivideModulo(i,t){var r,e,g=biNumBits(i),n=biNumBits(t),s=t.isNeg;if(g<n)return i.isNeg?((r=biCopy(bigOne)).isNeg=!t.isNeg,i.isNeg=!1,t.isNeg=!1,e=biSubtract(t,i),i.isNeg=!0,t.isNeg=s):(r=new BigInt,e=biCopy(i)),new Array(r,e);r=new BigInt,e=i;for(var d=Math.ceil(n/bitsPerDigit)-1,a=0;t.digits[d]<biHalfRadix;)t=biShiftLeft(t,1),++a,++n,d=Math.ceil(n/bitsPerDigit)-1;e=biShiftLeft(e,a),g+=a;for(var o=Math.ceil(g/bitsPerDigit)-1,u=biMultiplyByRadixPower(t,o-d);-1!=biCompare(e,u);)++r.digits[o-d],e=biSubtract(e,u);for(var b=o;d<b;--b){var l=b>=e.digits.length?0:e.digits[b],h=b-1>=e.digits.length?0:e.digits[b-1],f=b-2>=e.digits.length?0:e.digits[b-2],x=d>=t.digits.length?0:t.digits[d],c=d-1>=t.digits.length?0:t.digits[d-1];r.digits[b-d-1]=l==x?maxDigitVal:Math.floor((l*biRadix+h)/x);for(var v=r.digits[b-d-1]*(x*biRadix+c),m=l*biRadixSquared+(h*biRadix+f);m<v;)--r.digits[b-d-1],v=r.digits[b-d-1]*(x*biRadix|c),m=l*biRadix*biRadix+(h*biRadix+f);(e=biSubtract(e,biMultiplyDigit(u=biMultiplyByRadixPower(t,b-d-1),r.digits[b-d-1]))).isNeg&&(e=biAdd(e,u),--r.digits[b-d-1])}return e=biShiftRight(e,a),r.isNeg=i.isNeg!=s,i.isNeg&&(r=s?biAdd(r,bigOne):biSubtract(r,bigOne),e=biSubtract(t=biShiftRight(t,a),e)),0==e.digits[0]&&0==biHighIndex(e)&&(e.isNeg=!1),new Array(r,e)}function biDivide(i,t){return biDivideModulo(i,t)[0]}function biModulo(i,t){return biDivideModulo(i,t)[1]}function biMultiplyMod(i,t,r){return biModulo(biMultiply(i,t),r)}function biPow(i,t){for(var r=bigOne,e=i;0!=(1&t)&&(r=biMultiply(r,e)),0!=(t>>=1);)e=biMultiply(e,e);return r}function biPowMod(i,t,r){for(var e=bigOne,g=i,n=t;0!=(1&n.digits[0])&&(e=biMultiplyMod(e,g,r)),0!=(n=biShiftRight(n,1)).digits[0]||0!=biHighIndex(n);)g=biMultiplyMod(g,g,r);return e}function BarrettMu(i){this.modulus=biCopy(i),this.k=biHighIndex(this.modulus)+1;var t=new BigInt;t.digits[2*this.k]=1,this.mu=biDivide(t,this.modulus),this.bkplus1=new BigInt,this.bkplus1.digits[this.k+1]=1,this.modulo=BarrettMu_modulo,this.multiplyMod=BarrettMu_multiplyMod,this.powMod=BarrettMu_powMod}function BarrettMu_modulo(i){var t=biDivideByRadixPower(biMultiply(biDivideByRadixPower(i,this.k-1),this.mu),this.k+1),r=biSubtract(biModuloByRadixPower(i,this.k+1),biModuloByRadixPower(biMultiply(t,this.modulus),this.k+1));r.isNeg&&(r=biAdd(r,this.bkplus1));for(var e=0<=biCompare(r,this.modulus);e;)e=0<=biCompare(r=biSubtract(r,this.modulus),this.modulus);return r}function BarrettMu_multiplyMod(i,t){var r=biMultiply(i,t);return this.modulo(r)}function BarrettMu_powMod(i,t){var r=new BigInt;r.digits[0]=1;for(var e=i,g=t;0!=(1&g.digits[0])&&(r=this.multiplyMod(r,e)),0!=(g=biShiftRight(g,1)).digits[0]||0!=biHighIndex(g);)e=this.multiplyMod(e,e);return r}function encrypt(i,t,r){return setMaxDigits(19),encryptedString(new RSAKeyPair(i,"",t),r)}