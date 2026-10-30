"""
Script to generate test screenshot images for OCR and AI analysis verification.
Run: python samples/create_samples.py
"""
import os
from PIL import Image, ImageDraw, ImageFont

def generate_samples():
    samples_dir = os.path.dirname(__file__)

    # 1. Sample Concert Ticket
    img_ticket = Image.new('RGB', (800, 600), color=(60, 21, 36))
    d1 = ImageDraw.Draw(img_ticket)
    d1.rectangle([(40, 40), (760, 560)], outline=(229, 182, 195), width=3)
    d1.text((70, 70), "THE GARDEN • LIVE ADMISSION", fill=(229, 182, 195))
    d1.text((70, 130), "JAZZ AT THE GARDEN", fill=(255, 255, 255))
    d1.text((70, 200), "FRI 16 OCT 2026 • 19:30", fill=(229, 182, 195))
    d1.text((70, 260), "SECTION B  ROW 4  SEAT 12", fill=(255, 255, 255))
    d1.text((70, 320), "TOTAL: $45.00 USD", fill=(255, 255, 255))
    img_ticket.save(os.path.join(samples_dir, 'sample_ticket.png'))

    # 2. Sample Recipe
    img_recipe = Image.new('RGB', (800, 600), color=(250, 246, 240))
    d2 = ImageDraw.Draw(img_recipe)
    d2.text((70, 70), "CREAMY GARLIC PARMESAN PASTA", fill=(43, 32, 37))
    d2.text((70, 130), "Prep time: 15 mins • Servings: 2", fill=(121, 107, 112))
    d2.text((70, 190), "Ingredients:", fill=(43, 32, 37))
    d2.text((70, 230), "• 200g fettuccine pasta", fill=(43, 32, 37))
    d2.text((70, 270), "• 3 cloves minced garlic", fill=(43, 32, 37))
    d2.text((70, 310), "• 1 cup heavy cream", fill=(43, 32, 37))
    d2.text((70, 350), "• 1/2 cup grated parmesan cheese", fill=(43, 32, 37))
    d2.text((70, 410), "Simmer garlic in butter, add cream, boil pasta.", fill=(121, 107, 112))
    img_recipe.save(os.path.join(samples_dir, 'sample_recipe.png'))

    print("Generated sample_ticket.png and sample_recipe.png in samples/")

if __name__ == '__main__':
    generate_samples()

