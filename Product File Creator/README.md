## Product File Creator

#### Run the following command and wait till the output is saved in the same directory

```
python ProductsFileGenerator.py --barcodes "Packaging and Barcodes.xlsx" --colors colors.xlsx
```

#### Example output
```
C:\Users\Animeboynz\Documents\GitHub\KMD-Staff-Tools\Product File Creator>python ProductsFileGenerator.py --barcodes "Packaging and Barcodes.xlsx" --colors colors.xlsx
Processing Barcodes & Removing Duplicates: 100%|██████████████████████████████████| 365104/365104 [00:18<00:00, 19316.34it/s]
Would you like to remove all products that don't start with A, B, or a Number? (yes/no): yes
Adding Products to Protobuf: 100%|████████████████████████████████████████████████| 115802/115802 [00:05<00:00, 21823.60it/s]
Adding Colors to Protobuf: 100%|████████████████████████████████████████████████████████| 631/631 [00:00<00:00, 22534.47it/s]
✅ Protobuf file saved as products.proto.gz
```